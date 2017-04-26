package com.tigerspike.intelligence.exceptions;

/* IntelligenceInvalidParameterException.java - Intelligence SDK
*
* Captures all invalid parameters related Intelligence errors, extends IntelligenceException.
*
*/
public class IntelligenceInvalidParameterException extends IntelligenceException
{
    /// Enumeration to list the errors that can occur in the authentication module.
    public enum ErrorCode {
        ///Parameter is invalid.
        InvalidParameter(7001),
        ///Parameter is missing.
        MissingParameter(7002);

        private Integer code;

        ErrorCode(Integer codeNo) {
            code = codeNo;
        }

        public Integer getCode() {
            return code;
        }
    }

    public IntelligenceInvalidParameterException(ErrorCode errorCode, String message) {
        super(errorCode.getCode(), message);
    }

    public IntelligenceInvalidParameterException(String message) {
        super(message);
    }

    public IntelligenceInvalidParameterException(ErrorCode errorCode) {
        super(errorCode.getCode());
    }
}
