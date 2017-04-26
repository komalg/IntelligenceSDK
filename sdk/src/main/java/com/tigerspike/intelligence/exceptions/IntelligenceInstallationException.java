package com.tigerspike.intelligence.exceptions;

/* IntelligenceInstallationException.java - Intelligence SDK
*
* Captures all installation related Intelligence errors, extends IntelligenceException.
*
*/
public class IntelligenceInstallationException extends IntelligenceException {

    /// Enumeration to list the errors that can occur in the authentication module.
    public enum ErrorCode {
        /// Called 'create' method unnecessarily.
        AlreadyInstalledError(5001),
        /// Called 'update' method unnecessarily.
        AlreadyUpdatedError(5002);

        private Integer code;

        ErrorCode(Integer codeNo) {
            code = codeNo;
        }

        public Integer getCode() {
            return code;
        }
    }

    public IntelligenceInstallationException(ErrorCode errorCode, String message) {
        super(errorCode.getCode(), message);
    }

    public IntelligenceInstallationException(String message) {
        super(message);
    }

    public IntelligenceInstallationException(ErrorCode errorCode) {
        super(errorCode.getCode());
    }
}
