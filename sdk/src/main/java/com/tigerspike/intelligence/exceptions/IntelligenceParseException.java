package com.tigerspike.intelligence.exceptions;

/**
 * @Class IntelligenceParseException
 *
 * An exception thrown if there is a problem with parsing responses from server.
 * Exception has an errorCode and contains message with explanation
 *
 * Created by marcinowoc on 18/02/16.
 */
public class IntelligenceParseException extends IntelligenceException {

    public enum ErrorCode {
        /// Error to return when parsing JSON fails.
        ParseError(2001);

        private Integer code;
        ErrorCode(Integer codeNo) {
            code = codeNo;
        }

        public Integer getCode() {
            return code;
        }
    }

    public IntelligenceParseException() {
        super(ErrorCode.ParseError.getCode());
    }

    public IntelligenceParseException(String message) {
        super(ErrorCode.ParseError.getCode(), message);
    }
}
