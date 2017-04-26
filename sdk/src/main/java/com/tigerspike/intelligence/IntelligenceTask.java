package com.tigerspike.intelligence;

import com.tigerspike.intelligence.exceptions.IntelligenceException;
import com.tigerspike.intelligence.exceptions.IntelligenceRequestException;

abstract class IntelligenceTask implements Cloneable {

    private boolean mAutoRefreshToken = true;
    private TaskListener mTaskListener;
    // OAuth object is used for  validate and refresh token request creation on handleUnauthorizedError method
    // Please read the handleUnauthorizedError documentation for a full explanation of the default behavior.
    private OAuth mOAuth;

    // IntelligenceTask should be not initialized itself cause is used only as a root class for all the tasks in Intelligence SDK
    IntelligenceTask() {
    }

    IntelligenceTask(TaskListener taskListener) {
        mTaskListener = taskListener;
    }

    public void disableAutoRefreshToken() {
        mAutoRefreshToken = false;
    }

    public boolean autoRefreshToken() {
        return mAutoRefreshToken;
    }

    // To be executed on Main thread
    void onPreExecute() {
    }

    void onPostExecute() {
        if (mTaskListener != null) {
            mTaskListener.onTaskFinish(this);
        }
    }

    void onError(IntelligenceException intelligenceException) {
        if (mTaskListener != null) {
            mTaskListener.onTaskError(this, intelligenceException);
        }
    }

    // To be executed on Seperate thread
    abstract void execute() throws Exception;

    interface TaskListener {
        void onTaskFinish(IntelligenceTask intelligenceTask);

        void onTaskError(IntelligenceTask intelligenceTask, IntelligenceException exception);
    }

    public void setOAuth(OAuth oAuth){
        mOAuth = oAuth;
    }

    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * This function throws a sanitized error and is intended to be called by subclasses after the network request completes.
     * When the network request returns a 401 status code, the handling is delegated to the handleUnauthorizedError function.
     * Please read the handleUnauthorizedError documentation for a full explanation of the default behavior.
     *
     * @param response Response
     * @throws Exception
     */
    public void handleError(Response response) throws IntelligenceException {
        if (response.code() == HTTPStatusCode.UNAUTHORIZED.getStatusCode()) {
            handleUnauthorizedError(response);
        }
        else if (response.code() == HTTPStatusCode.FORBIDDEN.getStatusCode()) {
            handleForbiddenError();
        }
        else if (response.code() / 100 != 2) {
            handleUnhandledError(response.code());
        }
    }

    /**
     * This function is called when handleError recieves a 401.
     * It attempts to reauthenticate and then call the current operation again.
     * If authentication fails the function completes.
     *
     * @throws Exception
     */
    public void handleUnauthorizedError(Response response) throws IntelligenceException {
        if ( ! autoRefreshToken() ) {
            throw (new IntelligenceRequestException(IntelligenceRequestException.ErrorCode.Unauthorized, "You are not authenticated"));
        }

        try {
            OAuthCheckAuthenticationTask oAuthTask = new OAuthCheckAuthenticationTask(mOAuth);
            oAuthTask.execute();
            if (oAuthTask.isAuthenticated()) {
                // Retry task
                ((IntelligenceTask) this.clone()).execute();
            }
            else {
                // Could not renew / refresh token
                throw (new IntelligenceRequestException(IntelligenceRequestException.ErrorCode.Unauthorized, "You are not authenticated"));
            }
        }
        catch (IntelligenceException oAuthException) {
            throw oAuthException;
        }
        catch (Exception e) {
            throw (new IntelligenceRequestException(IntelligenceRequestException.ErrorCode.Unauthorized, "You are not authenticated")).addCause(e);
        }
    }

    public void handleForbiddenError() throws IntelligenceException {
        throw (new IntelligenceRequestException(IntelligenceRequestException.ErrorCode.Forbidden));
    }

    public void handleUnhandledError(Integer httpStatusCode) throws IntelligenceException {
        throw (new IntelligenceRequestException(IntelligenceRequestException.ErrorCode.UnhandledError, httpStatusCode.toString()));
    }

}
