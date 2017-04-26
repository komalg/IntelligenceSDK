package com.tigerspike.intelligence.exceptions;

/* IntelligenceRequestException.java - Intelligence SDK
*
* Captures all request related Intelligence errors, extends IntelligenceException.
*
*/
public class IntelligenceRequestException extends IntelligenceException {

    /// Enumeration to list the errors that can occur in the Configuration module.
    public enum ErrorCode {
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

        ErrorCode(Integer codeNo) {
            code = codeNo;
        }

        public Integer getCode() {
            return code;
        }
    }

    public IntelligenceRequestException(ErrorCode errorCode, String message) {
        super(errorCode.getCode(), message);
    }

    public IntelligenceRequestException(String message) {
        super(message);
    }

    public IntelligenceRequestException(ErrorCode errorCode) {
        super(errorCode.getCode());
    }
}
