package com.tigerspike.intelligence.exceptions;

/* IntelligenceIdentityException.java - Intelligence SDK
*
* Captures all identity related Intelligence errors, extends IntelligenceException.
*
*/
public class IntelligenceIdentityException extends IntelligenceException {

    /// Enumeration to list the errors that can occur in the identity module.
    public enum ErrorCode {
        /// The user is invalid.
        InvalidUserError(4001),
        /// The password provided is too weak. See `Intelligence.User` password field to see
        /// the security requirements of the password.
        WeakPasswordError(4002),
        /// The device token is invalid (zero length).
        DeviceTokenInvalidError(4003),
        /// Device token has not been registered yet.
        DeviceTokenNotRegisteredError(4004);
        private Integer code;

        ErrorCode(Integer codeNo) {
            code = codeNo;
        }

        public Integer getCode() {
            return code;
        }
    }

    public IntelligenceIdentityException(ErrorCode errorCode) {
        super(errorCode.getCode());
    }

    public IntelligenceIdentityException(ErrorCode errorCode, String message) {
        super(errorCode.getCode(), message);
    }

    public IntelligenceIdentityException(String message) {
        super(message);
    }
}
