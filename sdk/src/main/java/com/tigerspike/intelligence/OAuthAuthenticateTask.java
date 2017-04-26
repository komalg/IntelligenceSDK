package com.tigerspike.intelligence;

import android.accounts.AuthenticatorException;

import com.tigerspike.intelligence.OAuth.UserType;
import com.tigerspike.intelligence.exceptions.IntelligenceException;
import com.tigerspike.intelligence.exceptions.IntelligenceRequestException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class OAuthAuthenticateTask extends IntelligenceTask {

    private OAuth mOAuth;
    private String mUsername;
    private String mPassword;
    private UserType mUserType;

    public boolean success;
    public AuthenticationToken authenticationToken;
    public User user;

    public OAuthAuthenticateTask(String username, String password, UserType userType, OAuth oAuth, TaskListener taskListener) {
        super(taskListener);
        mOAuth = oAuth;
        mUsername = username;
        mPassword = password;
        mUserType = userType;
        disableAutoRefreshToken();
    }

    // We are reading the errorDescription and comparing it to plain text sentences
    // that have been defined. It is noted that this is not an ideal way to detect errors.
    // If the descriptions are changed on the server without updating the client then these
    // errors will not be detected.
    @Override
    public void handleUnauthorizedError(Response response) throws IntelligenceException {
        response.handleAuthenticationModuleErrorInResponse();
    }

    @Override
    void execute() throws Exception {

        Request request;

        if (mUserType == UserType.Application) {
            request = mOAuth.createAuthenticationRequest();
        } else {
            request = mOAuth.createAuthenticationRequest(mUsername, mPassword);
        }

        if (request == null) {
            throw (new AuthenticatorException("Could not create authentication request"));
        }

        Response response = request.execute();

        //If this is an error then exception is thrown and code execution is stopped
        handleError(response);

        try {
            authenticationToken = new AuthenticationToken(response.bodyData());
        } catch (JSONException jsonException) {
            throw (new IntelligenceRequestException(IntelligenceRequestException.ErrorCode.ParseError).addCause(jsonException));
        }

        if (mUserType == UserType.Application) {
            return;
        }

        // Try to get associated user object, for ApplicationUser we don't get a user object.
        request = mOAuth.createGetUserRequest(authenticationToken);

        if (request == null) {
            throw (new AuthenticatorException("Could not create get user request"));
        }

        response = request.execute();

        //If this is an error then exception is thrown and code execution is stopped
        handleError(response);

        try {
            JSONObject jsonObject = JSONUtils.parseSimpleJSONObject(response.bodyData());
            JSONArray jsonArray = jsonObject.getJSONArray("Data");
            user = new User(jsonArray.getJSONObject(0));
        } catch (JSONException jsonException) {
            throw (new IntelligenceRequestException(IntelligenceRequestException.ErrorCode.ParseError).addCause(jsonException));
        }

    }

    @Override
    void onPostExecute() {

        success = false;

        if (authenticationToken != null) {
            switch (mUserType) {
                case Application:
                    mOAuth.setApplicationUserToken(authenticationToken);
                    success = true;
                    break;
                case InternalUser:
                    if (user != null) {
                        mOAuth.setInternalUserToken(authenticationToken);
                        mOAuth.setInternalUser(user);
                        success = true;
                    }
                    break;
                case User:
                    if (user != null) {
                        mOAuth.setUserToken(authenticationToken);
                        mOAuth.setUser(user);
                        success = true;
                    }
            }
        }

        super.onPostExecute();

    }

}
