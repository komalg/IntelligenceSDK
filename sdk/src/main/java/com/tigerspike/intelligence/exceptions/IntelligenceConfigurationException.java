package com.tigerspike.intelligence.exceptions;

/* IntelligenceConfigurationException.java - Intelligence SDK
*
* Captures all configuration related Intelligence errors, extends IntelligenceException.
*
* Created by Mark van Rees on 21/07/15.
*/
public class IntelligenceConfigurationException extends IntelligenceException {

    /// Enumeration to list the errors that can occur in the Configuration module.
    public enum ErrorCode {
        /// Could not find json file.
        FileNotFound(1001),
        // Property parsed from json file is invalid.
        InvalidProperty(1002),
        /// Json properties file is invalid.
        InvalidFile(1003),
        /// Some required properties are missing in the configuration object.
        MissingProperty(1004);

        private Integer code;

        ErrorCode(Integer codeNo) {
            code = codeNo;
        }

        public Integer getCode() {
            return code;
        }
    }

    public IntelligenceConfigurationException(ErrorCode errorCode, String message) {
        super(errorCode.getCode(), message);
    }

    public IntelligenceConfigurationException(String message) {
        super(message);
    }

    public IntelligenceConfigurationException(ErrorCode errorCode) {
        super(errorCode.getCode());
    }
}
