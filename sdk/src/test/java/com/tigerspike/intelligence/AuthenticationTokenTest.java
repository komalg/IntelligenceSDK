package com.tigerspike.intelligence;

import junit.framework.TestCase;

import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Date;

/**
 * Created by markvanrees on 03/08/15.
 */
@RunWith(MockitoJUnitRunner.class)
public class AuthenticationTokenTest extends TestCase {

    /*
     * Check if manualy creation of AuthenticationToken through constructor works correctly
     */
    @Test
    public void setTokenManually() {

        Date expires = new Date(new Date().getTime() + 50 * 1000);

        AuthenticationToken authenticationToken = new AuthenticationToken("token","refreshtoken","bearer",expires);

        assertEquals("Token has wrong value", "token", authenticationToken.getToken());
        assertEquals("RefreshToken has wrong value", "refreshtoken", authenticationToken.getRefreshToken());
        assertEquals("Expires has wrong value", expires.getTime(), authenticationToken.getDateExpires().getTime());
        assertTrue("isExpires return wrong value", !authenticationToken.isExpired());
        assertFalse("requiresAuthentication return wrong value", authenticationToken.requiresAuthentication());

    }

    /*
     * Check if automatic creation of AuthenticationToken through constructor providing json data works correctly.
     */
    @Test
    public void setTokenFromJson() {

        String json = "{\"access_token\":\"abcde\",\"token_type\":\"bearer\",\"expires_in\":7200,\"refresh_token\":\"12345\"}";

        AuthenticationToken authenticationToken = null;

        try {
            authenticationToken = new AuthenticationToken(json);
        } catch (JSONException e) {
            fail("Error parsing json");
        }

        assertNotNull("Token not set", authenticationToken.getToken());
        assertEquals("Token has wrong value", "abcde", authenticationToken.getToken());

        assertNotNull("RefreshToken not set", authenticationToken.getRefreshToken());
        assertEquals("RefreshToken has wrong value", "12345", authenticationToken.getRefreshToken());

        assertNotNull("Expires not set", authenticationToken.getDateExpires());

        long expires = authenticationToken.getDateExpires().getTime();
        long shouldExpire = new Date().getTime() + 7200 * 1000;

        boolean expiresOk = Math.abs(shouldExpire-expires) < 10;

        assertTrue("expires value differs too much", expiresOk);

    }

    /*
     * Check if instance method invalidateToken correctly clears all fields.
     */
    @Test
    public void invalidateToken() {

        Date expires = new Date(new Date().getTime() + 50 * 1000);

        AuthenticationToken authenticationToken = new AuthenticationToken("token","refreshtoken","bearer", expires);

        assertNotNull("Token not set",authenticationToken.getToken());
        assertNotNull("RefreshToken not set",authenticationToken.getRefreshToken());
        assertNotNull("Tokentype not set", authenticationToken.getTokenType());
        assertNotNull("Expires not set", authenticationToken.getDateExpires());

        authenticationToken.invalidate();

        assertNull("Token not invalidated", authenticationToken.getToken());
        assertNull("RefreshToken not invalidated", authenticationToken.getRefreshToken());
        assertNull("TokenType not invalidated", authenticationToken.getTokenType());
        assertNull("Expires not invalidated", authenticationToken.getDateExpires());

    }



}