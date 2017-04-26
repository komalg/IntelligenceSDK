package com.tigerspike.intelligence;

import com.tigerspike.intelligence.exceptions.IntelligenceAuthenticationException;
import com.tigerspike.intelligence.exceptions.IntelligenceException;
import com.tigerspike.intelligence.exceptions.IntelligenceParseException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class IntelligenceStartupTask extends IntelligenceTask {

    private OAuth mOAuth;
    private Integer mSDKUserRole;
    private Integer mCompanyId;

    public IntelligenceStartupTask(OAuth oAuth, Configuration configuration, TaskListener taskListener) {
        super(taskListener);
        mOAuth = oAuth;
        mSDKUserRole = configuration.getSDKUserRole();
        mCompanyId = configuration.getCompanyID();
        disableAutoRefreshToken();
    }

    /*
     * Checks if token is still valid and / or needs refreshing. Clear token when no longer valid.
     */
    AuthenticationToken checkToken(AuthenticationToken authenticationToken) throws Exception {

        if (authenticationToken != null) {

            if (authenticationToken.isExpired()) {

                if (Utils.isNotEmpty(authenticationToken.getRefreshToken())) {

                    Response response = mOAuth.createRefreshTokenRequest(authenticationToken).execute();

                    if (isSuccess(response)) {
                        try {
                            return new AuthenticationToken(response.bodyData());
                        } catch (JSONException e) {
                            return null;
                        }
                    }

                } else {

                    return null;

                }

            } else {

                Response response = mOAuth.createValidateTokenRequest(authenticationToken).execute();

                if (isSuccess(response)) {

                    return authenticationToken;

                }
            }

        }

        return null;

    }



    protected void createInternalUser() throws Exception {

        AuthenticationToken authenticationToken = mOAuth.getApplicationUserToken();

        User user;
        int retries = 5;

        String password;

        do  {
            user = User.createRandom(mCompanyId);
            password = user.getPassword();

            Response response = mOAuth.createCreateUserRequest(authenticationToken, user).execute();

            if (isSuccess(response)) {

                try {

                    JSONObject jsonObject = JSONUtils.parseSimpleJSONObject(response.bodyData());
                    JSONArray jsonArray = jsonObject.getJSONArray("Data");
                    user = new User(jsonArray.getJSONObject(0));
                    user.setPassword(password);
                    if (user.getUserId() == null) {
                        user = null;
                    }
                } catch (JSONException e) {
                    // Error parsing response json
                    user = null;
                }

            } else {

                user = null;

            }

            retries--;

        } while (user == null && retries > 0);

        if (user == null) {

            throw new IntelligenceException("Could not create and SDK User account.");

        } else {

            Response response = mOAuth.createAssignRoleToUserRequest(authenticationToken, user, mSDKUserRole).execute();

            if (isSuccess(response)) {
                mOAuth.setInternalUser(user);
            } else {
                throw new IntelligenceException("Could not assign a role the the SDK user account.");
            }

        }

    }

    private void updateInternalUserToken() throws Exception {
        User internalUser = mOAuth.getInternalUser();

        Response response = mOAuth.createAuthenticationRequest(internalUser.getUsername(), internalUser.getPassword()).execute();

        AuthenticationToken authenticationToken = null;

        if (isSuccess(response)) {

            try {
                authenticationToken = new AuthenticationToken(response.bodyData());
            } catch (JSONException e) {
                throw(new IntelligenceException("Could not create SDK User token.").addCause(e));
            }

        }
        else {
            response.handleAuthenticationModuleErrorInResponse();
        }

        mOAuth.setInternalUserToken(authenticationToken);
    }

    /**
     * Requests to the server a new application user token.
     * @throws Exception
     */
    private void updateApplicationUserToken() throws IntelligenceException {
        Response response = mOAuth.createAuthenticationRequest().execute();

        if (isSuccess(response)) {

            try {
                AuthenticationToken authenticationToken = new AuthenticationToken(response.bodyData());
                if ( authenticationToken != null ) {
                    mOAuth.setApplicationUserToken(authenticationToken);
                }
                else {
                    throw new IntelligenceParseException("There was a JSON error while parsing the response of the OAuth call");
                }
            } catch (JSONException e) {
                throw new IntelligenceParseException("There was a JSON error while parsing the response of the OAuth call").addCause(e);
            }
        }
        else {
            response.handleAuthenticationModuleErrorInResponse();
        }

    }

    private boolean isSuccess(Response response) {
        return response != null && HTTPStatusCode.SUCCESS.getStatusCode().equals(response.code());
    }

    @Override
    void execute() throws Exception {

        mOAuth.clearApplicationUserToken();
        // Gets the APP user token.
        updateApplicationUserToken();

        // If we don't have an SDK user, create a new one.
        if (mOAuth.getInternalUser() == null) {
            createInternalUser();
        }

        // Given that we have an SDK user, refresh its token
        AuthenticationToken internalUserToken = checkToken(mOAuth.getInternalUserToken());

        // If the validate call fails, it will try to get a new token.
        if (internalUserToken == null) {
            try {
                updateInternalUserToken();
            }
            catch (IntelligenceAuthenticationException e) {
                // This SDK User can not be authenticated, so we need a new SDK User
                createInternalUser();
                updateInternalUserToken();
            }
        }
        else {
            // If we got the token store it.
            mOAuth.setInternalUserToken(internalUserToken);
        }

        // Check if a Real User token is present. If so validate it.
        if (mOAuth.getUserToken() != null) {

            AuthenticationToken userToken = checkToken(mOAuth.getUserToken());

            if (userToken == null) {
                mOAuth.clearUser();
            }
            else {
                mOAuth.setUserToken(userToken);
            }
        }
    }

}