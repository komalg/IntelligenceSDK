package com.tigerspike.intelligence;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.tigerspike.intelligence.exceptions.IntelligenceException;
import com.tigerspike.intelligence.exceptions.IntelligenceInvalidParameterException;

import org.json.JSONArray;
import org.json.JSONException;

import java.net.MalformedURLException;
import java.net.URL;

final class OAuth {

    public enum UserType {
        Application, InternalUser, User
    }

    // Storage keys
    private static final String KEY_APPLICATION_USER_TOKEN = "ApplicationUserToken";
    private static final String KEY_INTERNAL_USER_TOKEN = "InternalUserToken";
    private static final String KEY_USER_TOKEN = "UserToken";
    private static final String KEY_INTERNAL_USER = "InternalUser";
    private static final String KEY_USER = "User";

    private DataStore mDataStore;

    private AuthenticationToken mApplicationUserToken;
    private AuthenticationToken mInternalUserToken;
    private AuthenticationToken mUserToken;

    private User mInternalUser;
    private User mUser;

    private Configuration mConfiguration;
    private RequestURLBuilder mRequestURLBuilder;

    /**
     * Construct Authentication object with specified configuration.
     *
     * @param configuration configuration object
     * @param dataStore data store object
     */
    public OAuth(Configuration configuration, DataStore dataStore) {

        mConfiguration = configuration;
        mRequestURLBuilder = new RequestURLBuilder(mConfiguration);

        mDataStore = dataStore;

        // Get stored ApplicationUserToken
        String tokenString = mDataStore.get(KEY_APPLICATION_USER_TOKEN);
        if (tokenString != null) {
            try {
                mApplicationUserToken = new AuthenticationToken(tokenString);
            } catch (JSONException e) {
                // JSON is broken, erase stored token
                mDataStore.set(KEY_APPLICATION_USER_TOKEN,null,true);
            }
        }

        // Get stored InternalUserToken
        tokenString = mDataStore.get(KEY_INTERNAL_USER_TOKEN);
        if (tokenString != null) {
            try {
                mInternalUserToken = new AuthenticationToken(tokenString);
            } catch (JSONException e) {
                // JSON is broken, erase stored token
                mDataStore.set(KEY_INTERNAL_USER_TOKEN,null,true);
            }
        }

        // Get stored UserToken
        tokenString = mDataStore.get(KEY_USER_TOKEN);
        if (tokenString != null) {
            try {
                mUserToken = new AuthenticationToken(tokenString);
            } catch (JSONException e) {
                // JSON is broken, erase stored token
                mDataStore.set(KEY_USER_TOKEN,null,true);
            }
        }

        // Get stored Internal User
        String userString = mDataStore.get(KEY_INTERNAL_USER);
        if (userString != null) {
            try {
                mInternalUser = new User(userString);
            } catch (JSONException e) {
                // JSON is broken, erase stored user
                mDataStore.set(KEY_INTERNAL_USER,null,true);
            }
        }

        // Get stored User
        userString = mDataStore.get(KEY_USER);
        if (userString != null) {
            try {
                mUser = new User(userString);
            } catch (JSONException e) {
                // JSON is broken, erase stored user
                mDataStore.set(KEY_USER,null,true);
            }
        }


    }

    void setApplicationUserToken(AuthenticationToken authenticationToken) {
        mDataStore.set(KEY_APPLICATION_USER_TOKEN, authenticationToken != null ? authenticationToken.toJSONString() : null, true);
        mApplicationUserToken = authenticationToken;
    }

    AuthenticationToken getApplicationUserToken() {
        return mApplicationUserToken;
    }

    void clearApplicationUserToken() {
        setApplicationUserToken(null);
        setInternalUserToken(null);
    }

    void setInternalUserToken(AuthenticationToken authenticationToken) {
        mDataStore.set(KEY_INTERNAL_USER_TOKEN, authenticationToken != null ? authenticationToken.toJSONString() : null,true);
        mInternalUserToken = authenticationToken;
    }

    AuthenticationToken getInternalUserToken() {
        return mInternalUserToken;
    }

    void setUserToken(AuthenticationToken authenticationToken) {
        mDataStore.set(KEY_USER_TOKEN, authenticationToken != null ? authenticationToken.toJSONString() : null,true);
        mUserToken = authenticationToken;
    }

    AuthenticationToken getUserToken() {
        return mUserToken;
    }

    void setInternalUser(User user) {
        mDataStore.set(KEY_INTERNAL_USER, user != null ? user.toJSONString() : null,true);
        mInternalUser = user;
    }

    User getInternalUser() {
        return mInternalUser;
    }

    void clearInternalUser() {
        setInternalUser(null);
        setInternalUserToken(null);
    }

    void setUser(User user) {
        mDataStore.set(KEY_USER, user != null ? user.toJSONString() : null,true);
        mUser = user;
    }

    User getUser() {
        return mUser;
    }

    void clearUser() {
        setUser(null);
        setUserToken(null);
    }

    AuthenticationToken getCurrentAuthenticationToken() {
        return mUserToken != null ? mUserToken : mInternalUserToken != null ? mInternalUserToken : null;
    }

    void updateCurrentAuthenticationToken(AuthenticationToken authenticationToken) {

        if (mUserToken != null) {
            setUserToken(authenticationToken);
        } else if (mInternalUserToken != null) {
            setInternalUserToken(authenticationToken);
        }

    }

    Integer getCurrentUserId() {
        return mUser != null ? mUser.getUserId() : mInternalUser != null ? mInternalUser.getUserId() : null;
    }


    // Request creation

    private static final String KEY_AUTHENTICATION_TYPE_CLIENT_CREDENTIALS = "client_credentials";
    private static final String KEY_AUTHENTICATION_TYPE_USERNAME_PASSWORD = "password";
    private static final String KEY_AUTHENTICATION_TYPE_REFRESH_TOKEN = "refresh_token";

    private static final String KEY_PARAM_GRANT_TYPE = "grant_type";
    private static final String KEY_PARAM_CLIENT_ID = "client_id";
    private static final String KEY_PARAM_CLIENT_SECRET = "client_secret";
    private static final String KEY_PARAM_USERNAME = "username";
    private static final String KEY_PARAM_PASSWORD = "password";
    private static final String KEY_REFRESH_TOKEN = "refresh_token";

    //Authentication endpoints
    private static final String ENDPOINT_AUTHENTICATE = "token";
    private static final String ENDPOINT_REFRESH_TOKEN = "token";
    private static final String ENDPOINT_VALIDATE_TOKEN = "validate";
    //Identity endpoints
    private static final String ENDPOINT_GET_USER_ME = "providers/{PROVIDER_ID}/users/me";
    private static final String ENDPOINT_GET_USER_BY_ID = "companies/{COMPANY_ID}/users/{USER_ID}";
    private static final String ENDPOINT_CREATE_USER = "companies/{COMPANY_ID}/users";
    private static final String ENDPOINT_UPDATE_USER = "companies/{COMPANY_ID}/users";
    private static final String ENDPOINT_ASSIGN_ROLES = "projects/{PROJECT_ID}/assignrole";
    private static final String ENDPOINT_REVOKE_ROLES = "projects/{PROJECT_ID}/revokerole";


    Request createAuthenticationRequest() throws IntelligenceException {

        URL url = mRequestURLBuilder.authenticationBaseURL().urlPath(ENDPOINT_AUTHENTICATE).url();

        return RequestBuilder.POST(url)
                .accept(Constants.CONTENT_TYPE_JSON)
                .param(KEY_PARAM_GRANT_TYPE, KEY_AUTHENTICATION_TYPE_CLIENT_CREDENTIALS)
                .param(KEY_PARAM_CLIENT_ID, mConfiguration.getClientID())
                .param(KEY_PARAM_CLIENT_SECRET, mConfiguration.getClientSecret())
                .build();

    }

    Request createAuthenticationRequest(@NonNull String username, @NonNull String password) throws IntelligenceException {

        URL url = mRequestURLBuilder.authenticationBaseURL().urlPath(ENDPOINT_AUTHENTICATE).url();

        return RequestBuilder.POST(url)
                .accept(Constants.CONTENT_TYPE_JSON)
                .param(KEY_PARAM_GRANT_TYPE, KEY_AUTHENTICATION_TYPE_USERNAME_PASSWORD)
                .param(KEY_PARAM_CLIENT_ID, mConfiguration.getClientID())
                .param(KEY_PARAM_CLIENT_SECRET, mConfiguration.getClientSecret())
                .param(KEY_PARAM_USERNAME, username)
                .param(KEY_PARAM_PASSWORD, password)
                .build();

    }

    Request createRefreshTokenRequest(@NonNull AuthenticationToken authenticationToken) throws IntelligenceException {

        String refreshToken = authenticationToken.getRefreshToken();

        if (refreshToken == null || refreshToken.isEmpty()) {
            throw(new IntelligenceInvalidParameterException("No refresh token available"));
        }

        URL url = mRequestURLBuilder.authenticationBaseURL().urlPath(ENDPOINT_REFRESH_TOKEN).url();

        return RequestBuilder.POST(url)
                .accept(Constants.CONTENT_TYPE_JSON)
                .param(KEY_PARAM_GRANT_TYPE, KEY_AUTHENTICATION_TYPE_REFRESH_TOKEN)
                .param(KEY_PARAM_CLIENT_ID, mConfiguration.getClientID())
                .param(KEY_PARAM_CLIENT_SECRET, mConfiguration.getClientSecret())
                .param(KEY_REFRESH_TOKEN, refreshToken)
                .build();

    }

    Request createValidateTokenRequest(@NonNull AuthenticationToken authenticationToken) throws IntelligenceException {

        URL url = mRequestURLBuilder.authenticationBaseURL().urlPath(ENDPOINT_VALIDATE_TOKEN).url();

        return RequestBuilder.GET(url)
                .accept(Constants.CONTENT_TYPE_JSON)
                .authentication(authenticationToken)
                .build();

    }

    Request createGetUserRequest(@NonNull AuthenticationToken authenticationToken) throws IntelligenceException {

        URL url = mRequestURLBuilder.identityBaseURL().urlPath(ENDPOINT_GET_USER_ME)
                .addParam("PROVIDER_ID", Constants.PROVIDER_ID_PARAM)
                .url();

        return RequestBuilder.GET(url)
                .accept(Constants.CONTENT_TYPE_JSON)
                .authentication(authenticationToken)
                .build();

    }

    Request createGetUserByIdRequest(@NonNull AuthenticationToken authenticationToken, int id) throws IntelligenceException {

        URL url = mRequestURLBuilder.identityBaseURL().urlPath(ENDPOINT_GET_USER_BY_ID)
                .addParam("USER_ID", Integer.toString(id))
                .url();

        return RequestBuilder.GET(url)
                .accept(Constants.CONTENT_TYPE_JSON)
                .authentication(authenticationToken)
                .build();

    }

    Request createCreateUserRequest(@NonNull AuthenticationToken authenticationToken, @NonNull User user) throws IntelligenceException {

        URL url = mRequestURLBuilder.identityBaseURL().urlPath(ENDPOINT_CREATE_USER).url();

        return RequestBuilder.POST(url)
                .accept(Constants.CONTENT_TYPE_JSON)
                .authentication(authenticationToken)
                .body(new JSONArray().put(user.toJSONObject()))
                .build();

    }

    Request createUpdateUserRequest(@NonNull AuthenticationToken authenticationToken, @NonNull User user) throws IntelligenceException {

        URL url = mRequestURLBuilder.identityBaseURL().urlPath(ENDPOINT_UPDATE_USER)
                .addParam("USER_ID", String.valueOf(user.getUserId()))
                .url();

        return RequestBuilder.PUT(url)
                .accept(Constants.CONTENT_TYPE_JSON)
                .authentication(authenticationToken)
                .body(new JSONArray().put(user.toJSONObject()))
                .build();

    }

    Request createAssignRoleToUserRequest(@NonNull AuthenticationToken authenticationToken, @NonNull User user, int roleId) throws IntelligenceException {
        return createAssignRoleToUserRequest(authenticationToken, user.getUserId(), roleId);
    }

    Request createAssignRoleToUserRequest(@NonNull AuthenticationToken authenticationToken, int userId, int roleId) throws IntelligenceException {

        URL url = mRequestURLBuilder.identityBaseURL().urlPath(ENDPOINT_ASSIGN_ROLES)
                .url();

        Uri uri= Uri.parse(url.toExternalForm())
                .buildUpon()
                .appendQueryParameter("userid", String.valueOf(userId))
                .appendQueryParameter("roleid", mConfiguration.getSDKUserRole().toString())
                .build();

        URL queryUrl = null;
        try {
            queryUrl = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        JSONArray rolesJson = new JSONArray();
        rolesJson.put(roleId);

        return RequestBuilder.POST(queryUrl)
                .accept(Constants.CONTENT_TYPE_JSON)
                .authentication(authenticationToken)
                .body(rolesJson)
                .build();
    }

    Request createRevokeRoleFromUserRequest(@NonNull AuthenticationToken authenticationToken, @NonNull int userId, int roleId) throws IntelligenceException {

        URL url = mRequestURLBuilder.identityBaseURL().urlPath(ENDPOINT_REVOKE_ROLES)
                .url();

        Uri uri= Uri.parse(url.toExternalForm())
                .buildUpon()
                .appendQueryParameter("userid", Integer.toString(userId))
                        .appendQueryParameter("roleid", Integer.toString(roleId))
                        .build();

        URL queryUrl = null;
        try {
            queryUrl = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return RequestBuilder.DELETE(queryUrl)
                .accept(Constants.CONTENT_TYPE_JSON)
                .authentication(authenticationToken)
                .build();
    }
}
