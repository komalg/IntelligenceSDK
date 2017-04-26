package com.tigerspike.intelligence;

import com.tigerspike.intelligence.exceptions.IntelligenceInvalidParameterException;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.net.URL;

/**
 * Created by marcinowoc on 26/01/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class RequestURLBuilderTest extends TestCase {

    /*
     * Check if RequestURLBuilder constructor creates correct baseUrl for specified module, environment and region
     */
    @Test
    public void createIdentityBaseUrl() throws IntelligenceInvalidParameterException {

        Configuration configuration = new Configuration();
        configuration.setRegion(Region.Australia);
        configuration.setEnvironment(Environment.Production);

        RequestURLBuilder requestURLBuilder = new RequestURLBuilder(configuration);
        URL url = requestURLBuilder.identityBaseURL().url();
        assertEquals("https://identity.phoenixplatform.com.au/v2", url.toString());
    }

    /*
    * Check if RequestURLBuilder constructor creates correct baseUrl for specified module, environment and region
    */
    @Test
    public void createAnalysisBaseUrl() throws IntelligenceInvalidParameterException {
        Configuration configuration = new Configuration();
        configuration.setRegion(Region.Australia);
        configuration.setEnvironment(Environment.Production);

        RequestURLBuilder requestURLBuilder = new RequestURLBuilder(configuration);
        URL url = requestURLBuilder.analyticsBaseURL().url();
        assertEquals("https://analytics.phoenixplatform.com.au/v2", url.toString());
    }

    /*
     * Check if RequestURLBuilder constructor creates correct baseUrl for specified module, environment and region
     */
    @Test
    public void createLocationBaseUrl() throws IntelligenceInvalidParameterException {
        Configuration configuration = new Configuration();
        configuration.setRegion(Region.Australia);
        configuration.setEnvironment(Environment.Production);

        RequestURLBuilder requestURLBuilder = new RequestURLBuilder(configuration);
        URL url = requestURLBuilder.locationBaseURL().url();
        assertEquals("https://location.phoenixplatform.com.au/v2", url.toString());
    }

    /*
     * Check if RequestURLBuilder constructor creates correct baseUrl for specified module, environment and region
     */
    @Test
    public void createSingaporeUatAuthenticationBaseUrl() throws IntelligenceInvalidParameterException {

        Configuration configuration = new Configuration();
        configuration.setRegion(Region.Singapore);
        configuration.setEnvironment(Environment.UAT);

        RequestURLBuilder requestURLBuilder = new RequestURLBuilder(configuration);
        URL url = requestURLBuilder.authenticationBaseURL().url();
        assertEquals("https://authentication-uat.phoenixplatform.com.sg/v2", url.toString());
    }

    /*
     * Check if RequestURLBuilder constructor creates correct baseUrl for specified module, environment and region
     */
    @Test
    public void createSingaporeProductionAuthenticationBaseUrl() throws IntelligenceInvalidParameterException {

        Configuration configuration = new Configuration();
        configuration.setRegion(Region.Singapore);
        configuration.setEnvironment(Environment.Production);

        RequestURLBuilder requestURLBuilder = new RequestURLBuilder(configuration);
        URL url = requestURLBuilder.authenticationBaseURL().url();
        assertEquals("https://authentication.phoenixplatform.com.sg/v2", url.toString());
    }


    /*
     * Check if RequestURLBuilder constructor creates correct baseUrl for specified module, environment and region
     */
    @Test
    public void createEuropeUatAuthenticationBaseUrl() throws IntelligenceInvalidParameterException {

        Configuration configuration = new Configuration();
        configuration.setRegion(Region.Europe);
        configuration.setEnvironment(Environment.UAT);

        RequestURLBuilder requestURLBuilder = new RequestURLBuilder(configuration);
        URL url = requestURLBuilder.authenticationBaseURL().url();
        assertEquals("https://authentication-uat.phoenixplatform.eu/v2", url.toString());
    }

    /*
     * Check if RequestURLBuilder constructor creates correct baseUrl for specified module, environment and region
     */
    @Test
    public void createEuropeIntegrationAuthenticationBaseUrl() throws IntelligenceInvalidParameterException {

        Configuration configuration = new Configuration();
        configuration.setRegion(Region.Europe);
        configuration.setEnvironment(Environment.Integration);

        RequestURLBuilder requestURLBuilder = new RequestURLBuilder(configuration);
        URL url = requestURLBuilder.authenticationBaseURL().url();
        assertEquals("https://authentication-int.phoenixplatform.eu/v2", url.toString());
    }

    /*
     * Check if RequestURLBuilder constructor creates correct baseUrl for specified module, environment and region
     */
    @Test
    public void createEuropeStagingAuthenticationBaseUrl() throws IntelligenceInvalidParameterException {

        Configuration configuration = new Configuration();
        configuration.setRegion(Region.Europe);
        configuration.setEnvironment(Environment.Staging);

        RequestURLBuilder requestURLBuilder = new RequestURLBuilder(configuration);
        URL url = requestURLBuilder.authenticationBaseURL().url();
        assertEquals("https://authentication-staging.phoenixplatform.eu/v2", url.toString());
    }

    /*
     * Check if RequestURLBuilder constructor creates correct baseUrl for specified module, environment and region
     */
    @Test
    public void createEuropeLocalAuthenticationBaseUrl() throws IntelligenceInvalidParameterException {

        Configuration configuration = new Configuration();
        configuration.setRegion(Region.Europe);
        configuration.setEnvironment(Environment.Local);

        RequestURLBuilder requestURLBuilder = new RequestURLBuilder(configuration);
        URL url = requestURLBuilder.authenticationBaseURL().url();
        assertEquals("https://authentication-local.phoenixplatform.eu/v2", url.toString());
    }

    /*
     * Check if RequestURLBuilder constructor creates correct baseUrl for specified module, environment and region
     */
    @Test
    public void createEuropeDevelopmentAuthenticationBaseUrl() throws IntelligenceInvalidParameterException {

        Configuration configuration = new Configuration();
        configuration.setRegion(Region.Europe);
        configuration.setEnvironment(Environment.Development);

        RequestURLBuilder requestURLBuilder = new RequestURLBuilder(configuration);
        URL url = requestURLBuilder.authenticationBaseURL().url();
        assertEquals("https://authentication-dev.phoenixplatform.eu/v2", url.toString());
    }

    /*
     * Check if RequestURLBuilder constructor creates correct baseUrl for specified module, environment and region
     */
    @Test
    public void createEuropeProductionAuthenticationBaseUrl() throws IntelligenceInvalidParameterException {

        Configuration configuration = new Configuration();
        configuration.setRegion(Region.Europe);
        configuration.setEnvironment(Environment.Production);

        RequestURLBuilder requestURLBuilder = new RequestURLBuilder(configuration);
        URL url = requestURLBuilder.authenticationBaseURL().url();
        assertEquals("https://authentication.phoenixplatform.eu/v2", url.toString());
    }

    /*
     * Check if RequestURLBuilder constructor creates correct baseUrl for specified module, environment and region
     */
    @Test
    public void createUSUatAuthenticationBaseUrl() throws IntelligenceInvalidParameterException {

        Configuration configuration = new Configuration();
        configuration.setRegion(Region.UnitedStates);
        configuration.setEnvironment(Environment.UAT);

        RequestURLBuilder requestURLBuilder = new RequestURLBuilder(configuration);
        URL url = requestURLBuilder.authenticationBaseURL().url();
        assertEquals("https://authentication-uat.phoenixplatform.com/v2", url.toString());
    }

    /*
     * Check if RequestURLBuilder constructor creates correct baseUrl for specified module, environment and region
     */
    @Test
    public void createUSProductionAuthenticationBaseUrl() throws IntelligenceInvalidParameterException {

        Configuration configuration = new Configuration();
        configuration.setRegion(Region.UnitedStates);
        configuration.setEnvironment(Environment.Production);

        RequestURLBuilder requestURLBuilder = new RequestURLBuilder(configuration);
        URL url = requestURLBuilder.authenticationBaseURL().url();
        assertEquals("https://authentication.phoenixplatform.com/v2", url.toString());
    }

    /*
     * Check if RequestURLBuilder constructor creates correct baseUrl for specified module, environment and region
     */
    @Test
    public void createAustraliaUatAuthenticationBaseUrl() throws IntelligenceInvalidParameterException {

        Configuration configuration = new Configuration();
        configuration.setRegion(Region.Australia);
        configuration.setEnvironment(Environment.UAT);

        RequestURLBuilder requestURLBuilder = new RequestURLBuilder(configuration);
        URL url = requestURLBuilder.authenticationBaseURL().url();
        assertEquals("https://authentication-uat.phoenixplatform.com.au/v2", url.toString());
    }

    /*
     * Check if RequestURLBuilder constructor creates correct baseUrl for specified module, environment and region
     */
    @Test
    public void createAustraliaProductionAuthenticationBaseUrl() throws IntelligenceInvalidParameterException {

        Configuration configuration = new Configuration();
        configuration.setRegion(Region.Australia);
        configuration.setEnvironment(Environment.Production);

        RequestURLBuilder requestURLBuilder = new RequestURLBuilder(configuration);
        URL url = requestURLBuilder.authenticationBaseURL().url();
        assertEquals("https://authentication.phoenixplatform.com.au/v2", url.toString());
    }


}
