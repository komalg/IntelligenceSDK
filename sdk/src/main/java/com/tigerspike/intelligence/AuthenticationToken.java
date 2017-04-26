package com.tigerspike.intelligence;

import android.support.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

final class AuthenticationToken {

    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_TOKEN_TYPE = "token_type";
    private static final String KEY_EXPIRES_IN = "expires_in";
    private static final String KEY_REFRESH_TOKEN = "refresh_token";

    private String mToken;
    private String mTokenType;
    private String mRefreshToken;
    private Integer mUserID;
    private Date mDateExpires;

    /**
     * Creates AuthenticationToken from jsonData string.
     * Will throw JSONException if jsonData is incorrect.
     * JSONData is expected to contain: access_token, token_type, expires_in and optional refresh_token variable.
     *
     * @param jsonData json String containing token values
     * @throws JSONException
     */
    public AuthenticationToken(String jsonData) throws JSONException {

        JSONObject jsonObject = JSONUtils.parseSimpleJSONObject(jsonData);

        int expires_in;

        mToken = jsonObject.getString(KEY_ACCESS_TOKEN);
        mTokenType = jsonObject.getString(KEY_TOKEN_TYPE);
        expires_in = jsonObject.getInt(KEY_EXPIRES_IN);

        if (jsonObject.has(KEY_REFRESH_TOKEN)) {
            mRefreshToken = jsonObject.getString(KEY_REFRESH_TOKEN);
        }

        mDateExpires = new Date(new Date().getTime() + (expires_in*1000));

    }

    public JSONObject toJSONObject() {

        JSONObject jsonObject = new JSONObject();

        try {

            jsonObject.put(KEY_ACCESS_TOKEN, mToken);
            if (mRefreshToken != null) {
                jsonObject.put(KEY_REFRESH_TOKEN, mRefreshToken);
            }
            jsonObject.put(KEY_TOKEN_TYPE, mTokenType);
            jsonObject.put(KEY_EXPIRES_IN, (mDateExpires.getTime() - (new Date()).getTime()) / 1000);

        } catch (JSONException jsonException) {
            // Not much we can do here.
        }

        return jsonObject;

    }

    public String toJSONString() {
        JSONObject jsonObject = toJSONObject();
        return jsonObject != null ? jsonObject.toString() : null;
    }

    /**
     * Creates AuthenticationToken from provided token, refreshToken and dateExpires
     *
     * @param token token value
     * @param tokenType token type value
     * @param refreshToken refresh token value
     * @param dateExpires date on which token expires
     */
    public AuthenticationToken(String token, String refreshToken, String tokenType, Date dateExpires) {
        mToken = token;
        mTokenType = tokenType;
        mRefreshToken = refreshToken;
        mDateExpires = dateExpires;
    }

    /**
     *
     * @return Returns whether the authentication token is expired.
     */
    public boolean isExpired() {
        return mDateExpires == null || mDateExpires.before(new Date());
    }

    /**
     *
     * @return Returns when the authentication token will expire.
     */
    @Nullable
    public Date getDateExpires() {
        return mDateExpires;
    }

    /**
     * Return string containing the token.
     * Can return null if not set or invalidated.
     *
     * @return Return the token string.
     */
    public @Nullable String getToken() {
        return mToken;
    }

    /**
     *
     * @return Returns the token type string.
     */
    public @Nullable String getTokenType() {
        return mTokenType;
    }

    /**
     *
     * @return Returns the refresh token string.
     */
    public @Nullable String getRefreshToken() {
        return mRefreshToken;
    }

    /**
     * Invalidates AuthenticationToken. Resets all values to null;
     *
     */
    public void invalidate() {
        mToken = null;
        mTokenType = null;
        mRefreshToken = null;
        mDateExpires = null;
        mUserID = null;
    }

    public void setUserID(Integer userID) {
        mUserID = userID;
    }

    public Integer getUserID() {
        return mUserID;
    }

    /**
     *
     * @return Return whether the token needs has expired.
     */
    public boolean requiresAuthentication() {
        return mToken == null || mDateExpires == null || isExpired();
    }

}
