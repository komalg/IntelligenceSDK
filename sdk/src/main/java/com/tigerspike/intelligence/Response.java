package com.tigerspike.intelligence;


import android.util.Log;

import com.tigerspike.intelligence.exceptions.IntelligenceAuthenticationException;
import com.tigerspike.intelligence.exceptions.IntelligenceException;
import com.tigerspike.intelligence.exceptions.IntelligenceParseException;
import com.tigerspike.intelligence.exceptions.IntelligenceRequestException;

import org.json.JSONException;
import org.json.JSONObject;

final class Response {

    //keys for getting error and error description from response body
    protected static String bodyErrorKey = "error";
    protected static String bodyErrorDescriptionKey = "error_description";

    //strings needed to challenge server response body
    protected static String authenticationFailedString = "Authentication failed.";
    protected static String credentialIncorrectString = "Credentials incorrect.";
    protected static String accountDisabledString = "Account disabled.";
    protected static String accountLockedString = "Account locked.";
    protected static String invalidTokenString = "Invalid token.";
    protected static String invalidOrExpiredTokenString = "Token invalid or expired.";

    private static String ERROR_SDK_USER_3001 = "Unrecoverable error occurred during login, check credentials for Intelligence accounts.";
    private static String ERROR_SDK_USER_3002 = "Unrecoverable error occurred during login, the Intelligence account is disabled.";
    private static String ERROR_SDK_USER_3003 = "Unrecoverable error occurred during login, the Intelligence account is locked.";
    private static String ERROR_SDK_USER_3004 = "Unrecoverable error occurred during login, check credentials for Intelligence accounts.";
    private static String ERROR_SDK_USER_2005 = "Unrecoverable error occurred during login, check credentials for Intelligence accounts.";
    private static String ERROR_SDK_USER_2006 = "Unrecoverable error occurred during login, check credentials for Intelligence accounts.";


    private Request mRequest;
    private Exception mResponseException;
    private int mResponseCode;
    private String mBodyData;

    /**
     * Create response object with supplied values.
     *
     * @param request The associated Request object
     * @param errorCode int
     * @param bodyData String
     * @param exception Exception
     */
    public Response(Request request, Integer errorCode, String bodyData, Exception exception) {
        mRequest = request;
        mResponseException = exception;
        mResponseCode = errorCode;
        mBodyData = bodyData;
    }

    /**
     * Returns associated Request object of this Response
     *
     * @return Request
     */
    public Request request() {
        return mRequest;
    }

    /**
     * Return response exception
     *
     * @return Exception
     */
    public Exception exception() {
        return mResponseException;
    }

    /**
     * Return response code
     *
     * @return int
     */
    public int code() {
        return mResponseCode;
    }

    /**
     * @return true if the code is within the 200 HTTP status codes.
     */
    public boolean isSuccess() {
        return mResponseCode >= 200 && mResponseCode < 300;
    }


    /**
     * Return response body data as String if supplied.
     *
     * @return String
     */
    public String bodyData() {
        return mBodyData;
    }


    public void handleAuthenticationModuleErrorInResponse() throws IntelligenceException {
        assert(request().getURL().getHost().startsWith(Module.Authentication.toString()));

        if (HTTPStatusCode.UNAUTHORIZED.getStatusCode().equals(code())) {
            try {
                JSONObject jsonResponseObject = new JSONObject(bodyData());
                String error = jsonResponseObject.optString(bodyErrorKey);
                String errorDescription = jsonResponseObject.optString(bodyErrorDescriptionKey);

                if (error.equals(authenticationFailedString)) {
                    if (errorDescription.equals(credentialIncorrectString)) {
                        throw new IntelligenceAuthenticationException(IntelligenceAuthenticationException.ErrorCode.CredentialError);
                    }
                    else if (errorDescription.equals(accountDisabledString)) {
                        throw new IntelligenceAuthenticationException(IntelligenceAuthenticationException.ErrorCode.AccountDisabledError);
                    }
                    else if (errorDescription.equals(accountLockedString)) {
                        throw new IntelligenceAuthenticationException(IntelligenceAuthenticationException.ErrorCode.AccountLockedError);
                    }
                }
                else if (error.equals(invalidTokenString)) {
                    if (errorDescription.equals(invalidOrExpiredTokenString)) {
                        throw new IntelligenceAuthenticationException(IntelligenceAuthenticationException.ErrorCode.TokenInvalidOrExpired);
                    }
                }

                // Didn't recognise the values the server provided.
                // Normally in this situation we throw JSON parse exception, but here it is valid to have a response body
                // like forbidden 403, thus we throw IntelligenceRequestException
                throw new IntelligenceRequestException(IntelligenceRequestException.ErrorCode.Unauthorized,"You are unauthorized");

            }
            // Not catching the IntelligenceExceptions, those are handled by the caller.
            catch (JSONException e) {
                Log.d(OAuthCheckAuthenticationTask.class.getSimpleName(), "JSON exception", e);

                throw new IntelligenceParseException("There was a JSON error while parsing the response of the OAuth call").addCause(e);
            }
        }

        if (HTTPStatusCode.FORBIDDEN.getStatusCode().equals(code())) {
            throw(new IntelligenceRequestException(IntelligenceRequestException.ErrorCode.Forbidden, ERROR_SDK_USER_2005));
        }

        throw(new IntelligenceRequestException(IntelligenceRequestException.ErrorCode.UnhandledError, ERROR_SDK_USER_2006));
    }
}
