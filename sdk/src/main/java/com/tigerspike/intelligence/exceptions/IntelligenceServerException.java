package com.tigerspike.intelligence.exceptions;

import com.tigerspike.intelligence.HTTPStatusCode;

import org.json.JSONObject;

/* IntelligenceServerException.java - Intelligence SDK
*
* Captures all request related Intelligence errors, extends IntelligenceException.
*
*/
public class IntelligenceServerException extends IntelligenceException {

    /// Enumeration to list the errors that can occur in any request.
    public enum RequestError {
        /// Error to return when parsing JSON fails.
        ParseError(2001),
        /// Error to return if user doesn't have access to a particular API.
        AccessDeniedError(2002),
        /// Error to return if user is offline.
        InternetOfflineError(2003),
        /// Error to return if the user is not authenticated.
        Unauthorized(2004),
        /// Error to return if the user's role does not grant them access to this method.
        Forbidden(2005),
        /// Error to return if an error occurs that we can not handle.
        UnhandledError(2006);

        private Integer code;

        RequestError(Integer codeNo) {
            code = codeNo;
        }

        public Integer getStatusCode() {
            return code;
        }
    }

    private String mServerError;
    private String mServerErrorDescription;

    public IntelligenceServerException(Integer code, String serverError, String serverErrorDescription) {
        super(code, serverErrorDescription);
        mServerError = serverError;
        mServerErrorDescription = serverErrorDescription;
    }

    // TODO Remove this. Code is redundant with the Task handleError method.
    public static IntelligenceServerException fromResponse(Integer code, String data) {

        String serverError;
        String serverErrorDescription;

        try {
            JSONObject jsonObject = new JSONObject(data);
            serverError = jsonObject.getString("error");
            serverErrorDescription = jsonObject.getString("error_description");
        } catch (Exception e) {
            serverError = "";
            serverErrorDescription = data;
        }

        if (code == HTTPStatusCode.UNAUTHORIZED.getStatusCode()) {
            return new IntelligenceServerException(RequestError.Unauthorized.getStatusCode(), serverError, serverErrorDescription);
        } else if (code == HTTPStatusCode.FORBIDDEN.getStatusCode()) {
            return new IntelligenceServerException(RequestError.Forbidden.getStatusCode(), serverError, serverErrorDescription);
        } else if (code / 100 != 2) {
            return new IntelligenceServerException(RequestError.UnhandledError.getStatusCode(), serverError, serverErrorDescription);
        }

        return new IntelligenceServerException(code, serverError, serverErrorDescription);

    }

    public String getServerError() {
        return mServerError;
    }

    public String getServerErrorDescription() {
        return mServerErrorDescription;
    }

}
