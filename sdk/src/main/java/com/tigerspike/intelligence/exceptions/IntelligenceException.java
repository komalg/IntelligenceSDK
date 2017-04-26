package com.tigerspike.intelligence.exceptions;

import android.support.annotation.Nullable;

/* IntelligenceException.java - Intelligence SDK
*
* Base IntelligenceException class, every Exception thrown by Intelligence extends this class.
*
* Created by Mark van Rees on 21/07/15.
*/
public class IntelligenceException extends Exception {

    protected Integer mErrorCode;

    public IntelligenceException(int errorCode) {
        super();
        mErrorCode = errorCode;
    }

    public IntelligenceException(String message) {
        super(message);
    }

    public IntelligenceException(int errorCode, String message) {
        super(message);
        mErrorCode = errorCode;
    }

    public @Nullable Integer getErrorCode() {
        return mErrorCode;
    }

    public IntelligenceException addCause(Exception exception) {
        initCause(exception);
        return this;
    }

}
