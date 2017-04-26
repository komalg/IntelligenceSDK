package com.tigerspike.intelligence.exceptions;

import android.support.annotation.Nullable;

/* IntelligenceAuthenticationException.java - Intelligence SDK
*
* Captures all authentication related Intelligence errors, extends IntelligenceException.
*
*/
public class IntelligenceAuthenticationException extends IntelligenceException {

    /// Enumeration to list the errors that can occur in the authentication module.
    public enum ErrorCode {
        /// The client or user credentials are incorrect.
        CredentialError(3001),
        /// The account has been disabled.
        AccountDisabledError(3002),
        /// The account has been locked due to multiple authentication failures.
        /// An Administration is required to unlock.
        AccountLockedError(3003),
        /// The token is invalid or has expired.
        TokenInvalidOrExpired(3004);

        private Integer code;

        ErrorCode(Integer codeNo) {
            code = codeNo;
        }

        public Integer getCode() {
            return code;
        }
    }

    public IntelligenceAuthenticationException(ErrorCode errorCode) {
        super(errorCode.getCode());
    }

    public @Nullable ErrorCode getIntelligenceErrorCode() {
        for ( ErrorCode code : ErrorCode.values() ) {
            if (code.getCode().intValue() == getErrorCode().intValue() ) {
                return code;
            }
        }

        return null;
    }


    @Override
    public String getMessage() {
        ErrorCode code = getIntelligenceErrorCode();

        if ( code == null ) {
            return super.getMessage();
        }

        switch ( code ) {
            case CredentialError:
                return "Your credentials are invalid. Please check your configuration.";

            case AccountDisabledError:
                return "Your account has been disabled. Please contact support.";

            case AccountLockedError:
                return "Your account has been locked. Please contact support.";

            case TokenInvalidOrExpired:
                return "Your token is invalid or expired.";
        }
        return super.getMessage();
    }
}
