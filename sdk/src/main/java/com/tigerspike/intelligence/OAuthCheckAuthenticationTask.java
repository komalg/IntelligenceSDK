package com.tigerspike.intelligence;

import com.tigerspike.intelligence.exceptions.IntelligenceAuthenticationException;
import com.tigerspike.intelligence.exceptions.IntelligenceException;
import com.tigerspike.intelligence.exceptions.IntelligenceParseException;
import com.tigerspike.intelligence.exceptions.IntelligenceRequestException;

import org.json.JSONException;

/**
 * OAuthCheckAuthenticationTask is intended to re-authorised a token.
 * It is done by calling validate, refresh token and authenticate requests
 * This should work for any user ( real or internal).
 */
public class OAuthCheckAuthenticationTask extends IntelligenceTask {

    private OAuth mOAuth;

    private boolean isAuthenticated = false;

    public OAuthCheckAuthenticationTask(OAuth oAuth) {
        mOAuth = oAuth;
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
    void execute() throws IntelligenceException {
        isAuthenticated = false;
        boolean isRealUser = mOAuth.getUserToken() != null;
        AuthenticationToken userToken = isRealUser ? mOAuth.getUserToken() : mOAuth.getInternalUserToken();

        // If the user token is null we can't validate it or refresh it.
        if ( userToken != null ) {

            // Validate Request, if the token is not expired
            if (!userToken.isExpired()) {
                Response response = mOAuth.createValidateTokenRequest(userToken).execute();
                try{
                    handleError(response);

                    isAuthenticated = true;
                    return;
                }
                catch (IntelligenceAuthenticationException e) {
                    throw e;
                }
                catch (IntelligenceRequestException e) {
                    // If we received Unauthorized then we should continue, otherwise throw the IntelligenceRequestException
                    if (e.getErrorCode().intValue() != IntelligenceRequestException.ErrorCode.Unauthorized.getCode()) {
                        throw e;
                    }
                }
                catch (IntelligenceException e) {
                    clearUserAuth(isRealUser);
                    throw e;
                }
            }

            // Refresh Request
            try {
                Response refreshTokenResponse = mOAuth.createRefreshTokenRequest(userToken).execute();

                handleError(refreshTokenResponse);

                AuthenticationToken newToken = new AuthenticationToken(refreshTokenResponse.bodyData());

                if (isRealUser) {
                    mOAuth.setUserToken(newToken);
                } else {
                    mOAuth.setInternalUserToken(newToken);
                }

                isAuthenticated = true;
                return;
            }
            catch (IntelligenceAuthenticationException e) {
                throw e;
            }
            catch (IntelligenceRequestException e) {
                // If we received Unauthorized then we should continue, otherwise throw the IntelligenceRequestException
                if (e.getErrorCode().intValue() != IntelligenceRequestException.ErrorCode.Unauthorized.getCode()) {
                    throw e;
                }
            }
            catch (JSONException e) {
                clearUserAuth(isRealUser);
                throw new IntelligenceParseException("There was a JSON error while parsing the response of the OAuth call").addCause(e);
            }
            catch (IntelligenceException e) {
                clearUserAuth(isRealUser);
                throw e;
            }

        }

        User user = isRealUser ? mOAuth.getUser() : mOAuth.getInternalUser();

        if ( user != null ) {
            // Token Request
            try {
                Response authenticationResponse = mOAuth.createAuthenticationRequest(user.getUsername(), user.getPassword()).execute();

                handleError(authenticationResponse);//This can throw exception and stop code execution

                AuthenticationToken newToken = new AuthenticationToken(authenticationResponse.bodyData());

                if (isRealUser) {
                    mOAuth.setUserToken(newToken);
                } else {
                    mOAuth.setInternalUserToken(newToken);
                }

                isAuthenticated = true;
                return;
            }
            catch (JSONException e) {
                clearUserAuth(isRealUser);
                throw new IntelligenceParseException("There was a JSON error while parsing the response of the OAuth call").addCause(e);
            }
            catch (IntelligenceException e) {
                clearUserAuth(isRealUser);
                throw e;
            }
        }
        else {
            throw new IntelligenceRequestException(IntelligenceRequestException.ErrorCode.Unauthorized, "You are unauthorized");
        }
    }

    private void clearUserAuth(boolean isRealUser) {
        if (isRealUser) {
            mOAuth.clearUser();
        }
        else {
            mOAuth.clearInternalUser();
        }
    }

    /**
     * @return after executing, true if the OAuth token was deemed valid by the server.
     */
    public boolean isAuthenticated() {
        return this.isAuthenticated;
    }
}
