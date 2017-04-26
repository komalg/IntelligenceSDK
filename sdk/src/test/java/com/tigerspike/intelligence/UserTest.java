package com.tigerspike.intelligence;

import com.tigerspike.intelligence.exceptions.IntelligenceIdentityException;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Created by Mel on 10/08/15.
 */
@RunWith(MockitoJUnitRunner.class)
public class UserTest extends TestCase
{
    /**
     * PSDK-84
     * Test Case 1
     * Given a valid Password (i.e. it has more than 8 characters, at least 1 capital letter and 1 number)
     * The Password is accepted as valid
     */
    @Test
    public void testValidPassword() {
        String password = "PassWord123";

        try {
            User.validatePassword(password);
        } catch (IntelligenceIdentityException e) {
            fail("Password is ok, exception wrongly thrown, check implementation of password validation");
        }

    }

    /**
     * PSDK-84
     * Test Case 2
     * Given an invalid Password (i.e. it has  6 characters , at least 1 capital letter and 1 number)
     * The Password is accepted as valid
     */
    @Test
    public void testLessThanEightCharactersPassword() {
        String password = "Pass123";

        try {
            User.validatePassword(password);
        } catch (IntelligenceIdentityException e) {
            return;
        }

        fail("Password is wrong, an exception should be thrown, check implementation of password validation");
    }
    /**
     * PSDK-84
     * Test Case 3
     * Given an invalid Password (i.e. it has more than 8 characters, with no capital letter and 1 number)
     * The Password is accepted as valid
     */
    @Test
    public void testNoCapitalLetterPassword() {
        String password = "password123";

        try {
            User.validatePassword(password);
        } catch (IntelligenceIdentityException e) {
            return;
        }

        fail("Password is wrong, an exception should be thrown, check implementation of password validation");
    }

    /**
     * PSDK-84
     * Test Case 4
     * Given an invalid Password (i.e. it has more than 8 characters, with at least 1 capital letter and no number )
     * The Password is accepted as valid
     */
    @Test
    public void testNoNumberPassword() {
        String password = "PassWordNoNumber";

        try {
            User.validatePassword(password);
        } catch (IntelligenceIdentityException e) {
            return;
        }

        fail("Password is wrong, an exception should be thrown, check implementation of password validation");
    }
}
