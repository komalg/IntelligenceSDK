package com.tigerspike.intelligence;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

class Request {

    private Method mMethod;
    private RequestSecurityPolicy mRequestSecurityPolicy;
    private URL mUrl;
    private String mBody;
    private HashMap<String, String> mHeaders;
    private HashMap<String, String> mParams;

    public enum Method {

        GET("GET"), POST("POST"), PUT("PUT"), DELETE("DELETE");

        private String mName;

        Method(String name) {
            mName = name;
        }

        @Override
        public String toString() {
            return mName;
        }

    }

    /**
     * Creates a Request with specified HTTP Method, url, headers and parameters
     *
     * @param method  HTTP Method of request : GET, POST, UPDATE, DELETE
     * @param url     url for request
     * @param headers HashMap<String, String> containing optional headers to be sent with request
     * @param params  HashMap<String,String> containing optional parameters to be sent with request
     */
    public Request(Method method, URL url, HashMap<String, String> headers, HashMap<String, String> params) {
        mMethod = method;
        mUrl = url;
        mHeaders = headers;
        mParams = params;
    }

    public Request(Method method, URL url, HashMap<String, String> headers, HashMap<String, String> params, String body) {
        mMethod = method;
        mBody = body;
        mUrl = url;
        mHeaders = headers;
        mParams = params;
    }

    /**
     * Copy constructor. Clones the request into a new Request object.
     *
     * @param request Request
     */
    public Request(Request request) {
        mMethod = request.mMethod;
        mUrl = request.mUrl;
        mBody = request.mBody;
        mHeaders = request.mHeaders;
        mParams = request.mParams;
    }

    /**
     * Returns the HTTP Method of the request
     *
     * @return Request.Method
     */
    public Method getMethod() {
        return mMethod;
    }

    /**
     * Return the URL of the request
     *
     * @return URL
     */
    public URL getURL() {
        return mUrl;
    }

    /**
     * Returns a HashMap<String,String> containing the http headers for this request.
     *
     * @return parameters <String,String> HashMap
     */
    public HashMap<String, String> getHeaders() {
        return mHeaders;
    }

    /**
     * Returns a HashMap<String,String> containing the params for this request.
     *
     * @return parameters <String,String> HashMap
     */
    public HashMap<String, String> getParams() {
        return mParams;
    }

    /**
     * Set custom RequestSecurityPolicy to apply security policy for HttpsConnection.
     * @param RequestSecurityPolicy securityPolicy to be set.
     */
    public void setRequestSecurityPolicy(RequestSecurityPolicy securityPolicy){
        mRequestSecurityPolicy = securityPolicy;
    }

    /**
     * Executes the request and returns a Response Object containing the Result of the request.
     *
     * @return Response
     */
    public Response execute() {

        HttpsURLConnection conn = null;
        InputStream inputStream = null;
        OutputStream outputStream = null;
        Exception exception = null;

        int responseCode = -1;
        String responseBody = null;


        try {

            if (Thread.interrupted()) {
                throw new InterruptedException();
            }

            if (mParams == null) {
                mParams = new HashMap<>();
            }

            if (mHeaders == null) {
                mHeaders = new HashMap<>();
            }

            if ((mMethod == Method.GET || mMethod == Method.DELETE) && mParams.size() > 0) {

                // Add Parameters to URL
                StringBuilder urlWithParams = new StringBuilder();

                for (Map.Entry<String, String> entry : mParams.entrySet()) {
                    if (urlWithParams.length() == 0) {
                        urlWithParams.append("?");
                    } else {
                        urlWithParams.append("&");
                    }
                    urlWithParams.append(entry.getKey()).append("=").append(entry.getValue());
                }

                urlWithParams.insert(0, mUrl.toString());

                conn = (HttpsURLConnection) new URL(urlWithParams.toString()).openConnection();

            } else {
                conn = (HttpsURLConnection) mUrl.openConnection();
            }


            conn.setReadTimeout(Constants.CONNECTION_RESPONSE_TIMEOUT_MS);
            conn.setConnectTimeout(Constants.CONNECTION_TIMEOUT_MS);

            conn.setDoInput(true);
            if (mMethod == Method.POST || mMethod == Method.PUT) {
                conn.setDoOutput(true);
            }

            conn.setUseCaches(false);
            conn.setRequestMethod(mMethod.toString());

            if(mRequestSecurityPolicy != null){
                mRequestSecurityPolicy.applySecurityPolicy(conn);
            }

            mHeaders.put("Connection", "close");

            for (Map.Entry<String, String> entry : mHeaders.entrySet()) {
                conn.setRequestProperty(entry.getKey(), entry.getValue());
            }

            if (Thread.interrupted()) {
                throw new InterruptedException();
            }

            if (mMethod == Method.POST || mMethod == Method.PUT) {

                if (mBody == null) {

                    StringBuilder body = new StringBuilder();

                    for (Map.Entry<String, String> entry : mParams.entrySet()) {
                        if (body.length() > 0) {
                            body.append("&");
                        }
                        body.append(entry.getKey()).append("=").append(entry.getValue());
                    }

                    mBody = body.toString();

                    conn.setRequestProperty("Content-Type", Constants.CONTENT_TYPE_FORM_URL_ENCODED);

                } else {

                    conn.setRequestProperty("Content-Type", Constants.CONTENT_TYPE_JSON);

                }

                outputStream = conn.getOutputStream();
                outputStream.write(mBody.getBytes());
                outputStream.flush();

            }

            if (Thread.interrupted()) {
                throw new InterruptedException();
            }

            responseCode = conn.getResponseCode();

            //if there is success response then take inputstream otherwise get error stream
            if (responseCode/100 != 2 ) {
                inputStream = conn.getErrorStream();
            } else {
                inputStream = conn.getInputStream();
            }

            if (inputStream != null) {
                responseBody = readStream(inputStream);
            }

        } catch (Exception e) {
            exception = e;
        } finally {

            if (conn != null) {
                conn.disconnect();
            }

            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    // Nothing much we can do here
                }
            }

            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    // Nothing much we can do here
                }
            }

        }

        return new Response(this, responseCode, responseBody, exception);

    }

    /**
     * Converts an inputStream input a byte array.
     *
     * @param inputStream stream to be converted to String
     * @return String inputstream as string
     * @throws IOException
     */
    public static String readStream(InputStream inputStream) throws IOException {

        byte[] inputBuffer = new byte[256];

        ByteArrayOutputStream outputBuffer = new ByteArrayOutputStream();

        int result = inputStream.read(inputBuffer);
        while (result != -1) {
            outputBuffer.write(inputBuffer, 0, result);
            result = inputStream.read(inputBuffer);
        }

        return outputBuffer.toString();

    }

}
