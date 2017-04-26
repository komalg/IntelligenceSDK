package com.tigerspike.intelligence;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

final class RequestBuilder {

    private URL mUrl;
    private String mBody;
    private Request.Method mMethod;
    private static RequestSecurityPolicy mRequestSecurityPolicy;
    private HashMap<String, String> mHeaders;
    private HashMap<String, String> mParameters;

    private RequestBuilder(Request.Method method, URL url) {
        mMethod = method;
        mUrl = url;
        mHeaders = new HashMap<>();
        mParameters = new HashMap<>();
    }

    private RequestBuilder(Request.Method method, String url) throws MalformedURLException{
        this(method, new URL(url));
    }

    /**
     * Builds and returns a Request object.
     *
     * @return Returns a Request object.
     */
    public Request build() {
        Request request = new Request(mMethod, mUrl, mHeaders, mParameters, mBody);
        if (mRequestSecurityPolicy != null){
            request.setRequestSecurityPolicy(mRequestSecurityPolicy);
        }
        return request;
    }

    /**
     * Creates a new RequestBuilder with supplied url and Method type GET
     *
     * @param url
     * @return Return a RequestBuilder object.
     */
    public static RequestBuilder GET(URL url) {
        return new RequestBuilder(Request.Method.GET, url);
    }

    /**
     * Creates a new RequestBuilder with supplied url and Method type POST
     *
     * @param url
     * @return Return a RequestBuilder object.
     */
    public static RequestBuilder POST(URL url) {
        return new RequestBuilder(Request.Method.POST, url);
    }

    /**
     * Creates a new RequestBuilder with supplied url and Method type PUT
     *
     * @param url
     * @return Return a RequestBuilder object.
     */
    public static RequestBuilder PUT(URL url) {
        return new RequestBuilder(Request.Method.PUT, url);
    }

    /**
     * Creates a new RequestBuilder with supplied url and Method type DELETE
     *
     * @param url
     * @return Return a RequestBuilder object.
     */
    public static RequestBuilder DELETE(URL url) {
        return new RequestBuilder(Request.Method.DELETE, url);
    }

    /**
     * Creates a new RequestBuilder with supplied url and Method type GET
     * Throws MalformedURLException
     *
     * @param url
     * @return Return a RequestBuilder object.
     * @throws MalformedURLException
     */
    public static RequestBuilder GET(String url) throws MalformedURLException {
        return new RequestBuilder(Request.Method.GET, url);
    }

    /**
     * Creates a new RequestBuilder with supplied url and Method type POST
     * Throws MalformedURLException
     *
     * @param url String
     * @return RequestBuilder
     * @throws MalformedURLException
     */
    public static RequestBuilder POST(String url) throws MalformedURLException {
        return new RequestBuilder(Request.Method.POST, url);
    }

    /**
     * Creates a new RequestBuilder with supplied url and Method type PUT
     * Throws MalformedURLException
     *
     * @param url String
     * @return RequestBuilder
     * @throws MalformedURLException
     */
    public static RequestBuilder PUT(String url) throws MalformedURLException {
        return new RequestBuilder(Request.Method.PUT, url);
    }

    /**
     * Creates a new RequestBuilder with supplied url and Method type DELETE
     * Throws MalformedURLException
     *
     * @param url String
     * @return RequestBuilder
     * @throws MalformedURLException
     */
    public static RequestBuilder DELETE(String url) throws MalformedURLException {
        return new RequestBuilder(Request.Method.DELETE, url);
    }

    /**
     * Set custom RequestSecurityPolicy for further building Request.
     * @param RequestSecurityPolicy securityPolicy to be set.
     */
    public static void setRequestSecurityPolicy(RequestSecurityPolicy securityPolicy){
        mRequestSecurityPolicy = securityPolicy;
    }

    /**
     * Get currently used RequestSecurityPolicy in RequestBuilder.
     * @return  RequestSecurityPolicy policyChecker.
     */
    public static RequestSecurityPolicy getRequestSecurityPolicy(){
        return mRequestSecurityPolicy;
    }

    /**
     * Add "Accept" header with supplied value
     * @param accept String
     * @return RequestBuilder
     */
    public RequestBuilder accept(String accept) {
        mHeaders.put("Accept", accept);
        return this;
    }

    /**
     * Adds header with supplied name and value
     *
     * @param headerName String
     * @param headerValue String
     * @return RequestBuilder
     */
    public RequestBuilder header(String headerName, String headerValue) {
        mHeaders.put(headerName, headerValue);
        return this;
    }

    /**
     * Adds param with supplied name and value
     *
     * @param paramName
     * @param paramValue
     * @return RequestBuilder
     */
    public RequestBuilder param(String paramName, String paramValue) {
        if (Utils.isEmpty(paramValue)) {
            mParameters.remove(paramName);
        } else {
            mParameters.put(paramName, paramValue);
        }
        return this;
    }

    public RequestBuilder param(String paramName, Object paramValue) {
        if (paramValue == null) {
            mParameters.remove(paramName);
        } else {
            return param(paramName, String.valueOf(paramValue));
        }
        return this;
    }

    /**
     * Adds token as Authorisation Header
     * @param authenticationToken to add as authorization header
     * @return
     */
    public RequestBuilder authentication(AuthenticationToken authenticationToken) {

        if (authenticationToken == null) {
            return this;
        }

        String token = authenticationToken.getToken();
        String tokenType = authenticationToken.getTokenType();

        if (Utils.isNotEmpty(tokenType) && Utils.isNotEmpty(token)) {
            mHeaders.put("Authorization", Utils.upperFirst(tokenType) + " " + token);
        }

        return this;

    }


    public RequestBuilder body(String body) {
        mBody = body;
        return this;
    }

    public RequestBuilder body(JSONObject body) {
        mBody = body.toString();
        return this;
    }

    public RequestBuilder body(JSONArray body) {
        mBody = body.toString();
        return this;
    }

}
