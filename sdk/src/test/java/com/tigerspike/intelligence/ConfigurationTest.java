package com.tigerspike.intelligence;

import android.app.Application;
import android.content.res.AssetManager;

import com.tigerspike.intelligence.exceptions.IntelligenceConfigurationException;

import junit.framework.TestCase;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Created by Mel on 23/07/15.
 */
@RunWith(MockitoJUnitRunner.class)
public class ConfigurationTest extends TestCase
{

    public AssetManager mockAssetManager;
    public Application mockApplication;

    /*
    * Initializes mock objects needed to perform Configuration tests
    * */
    @Before
    public void init() {

        mockAssetManager = Mockito.mock(AssetManager.class);

        try {
            Mockito.when(mockAssetManager.open(Mockito.anyString())).thenReturn(IOUtils.toInputStream("{\"region\":\"US\",\"environment\":\"uat\",\"client_id\":\"AndroidIntelligenceSDKApp_p\",\"client_secret\":\"G1Ep4NU9Tp1Myp726oseqtcmxiopxpxhwzkonyxu\",\"application_id\":\"10069\",\"project_id\":\"40003\",\"company_id\":\"19017\",\"sdk_user_role\":16018,\"certificate_trust_policy\": \"any\"}"));
        } catch (Exception e) {}

        mockApplication = Mockito.mock(Application.class);
        Mockito.when(mockApplication.getAssets()).thenReturn(mockAssetManager);

    }

    /*
    * Tests if JSON file is read correctly and all parameters are set as expected
    * */
    @Test
    public void ReadingJson() {

        Configuration configuration = new Configuration();

        try {
            configuration.loadFromJSON(mockApplication, "intelligence.json");
        } catch (IntelligenceConfigurationException e) {
            fail(e.getMessage());
        }

        assertEquals("Region is not consistent with configuration file", configuration.getRegion(), Region.UnitedStates);
        assertEquals("ClientID is not consistent with configuration file", configuration.getClientID(), "AndroidIntelligenceSDKApp_p");
        assertEquals("Environment is not consistent with configuration file", configuration.getEnvironment(), Environment.UAT);
        assertEquals("ClientSecret is not consistent with configuration file", configuration.getClientSecret(), "G1Ep4NU9Tp1Myp726oseqtcmxiopxpxhwzkonyxu");
        assertEquals("ApplicationID is not consistent with configuration file", configuration.getApplicationID().intValue(),10069);
        assertEquals("ProjectID is not consistent with configuration file", configuration.getProjectID().intValue(), 40003);
        assertEquals("CompanyID is not consistent with configuration file", configuration.getCompanyID().intValue(), 19017);
        assertEquals("SDK User role is not consistent with configuration file", configuration.getSDKUserRole().intValue(), 16018);
        assertEquals("CertificateTrustPolicy is not consistent with configuration file", configuration.getCertificateTrustPolicy(), CertificateTrustPolicy.Any);
    }

    /*
    * Test Case 1 , PSDK-31
    * Given there is a manual configuration object, And the configuration has a Intelligence Region value set to 'US'
    * the SDK will target the related endpoint
    * */
    @Test
    public void RegionUrlUS() {
        Configuration configuration = new Configuration();
        assertNotNull(configuration);

        configuration.setRegion(Region.UnitedStates);
        assertEquals("Region is not United Stated", configuration.getRegion().getExtension(), Region.UnitedStates.getExtension());
    }

    /*
    * Test Case 2 , PSDK-31
    * Given there is a manual configuration object, And the configuration has a Intelligence Region value set to 'EU'
    * the SDK will target the related endpoint
    * */
    @Test
    public void RegionUrlEU() {
        Configuration configuration = new Configuration();
        assertNotNull(configuration);

        configuration.setRegion(Region.Europe);
        assertEquals("Region is not Europe", configuration.getRegion().getExtension(), Region.Europe.getExtension());
    }

    /*
    * Test Case 3 , PSDK-31
    * Given there is a manual configuration object, And the configuration has a Intelligence Region value set to 'SG'
    * the SDK will target the related endpoint
    * */
    @Test
    public void RegionUrlSG() {
        Configuration configuration = new Configuration();
        assertNotNull(configuration);

        configuration.setRegion(Region.Singapore);
        assertEquals("Region is not Singapore", configuration.getRegion().getExtension(), Region.Singapore.getExtension());
    }

    /*
    * Test Case 4 , PSDK-31
    * Given there is a manual configuration object, And the configuration has a Intelligence Region value set to 'US'
    * the SDK will target the related endpoint
    * */
    @Test
    public void RegionUrlAU() {
        Configuration configuration = new Configuration();
        assertNotNull(configuration);

        configuration.setRegion(Region.Australia);
        assertEquals("Region is not Australia", configuration.getRegion().getExtension(), Region.Australia.getExtension());
    }

    /*
    * Test Case 5, PSDK-31
    * Given there is a manual configuration object, And the config fields from the config are valid apart from the Client Id which is left null
    * Then there should be an exception pointing out that a mandatory field is empty
    * */
    @Test
    public void ConfigClientIdMissing() {

        Configuration configuration = new Configuration();
        assertNotNull(configuration);

        configuration.setRegion(Region.Singapore);
        configuration.setClientSecret("secret");
        configuration.setProjectID(666);
        configuration.setApplicationID(667);
        configuration.setCompanyID(3);

        assertEquals("ClientID is missing in Configuration Object", true, configuration.hasMissingProperty());
    }

    /*
    * Test Case 6, PSDK-31
    * Given there is a manual configuration object, And the config fields from the config are valid apart from the Client Secret which is left null
    * Then there should be an exception pointing out that a mandatory field is empty
    * */
    @Test
    public void ConfigClientSecretMissing() {

        Configuration configuration = new Configuration();
        assertNotNull(configuration);

        configuration.setRegion(Region.Singapore);
        configuration.setClientID("id");
        configuration.setProjectID(666);
        configuration.setApplicationID(667);
        configuration.setCompanyID(3);

        assertEquals("ClientSecret is missing in Configuration Object", true, configuration.hasMissingProperty());
    }

    /*
    * Test Case 7, PSDK-31
    * Given there is a manual configuration object, And the config fields from the config are valid apart from the Project Id which is left null
    * Then there should be an exception pointing out that a mandatory field is empty
    * */
    @Test
    public void ConfigProjectIdMissing() {

        Configuration configuration = new Configuration();
        assertNotNull(configuration);

        configuration.setRegion(Region.Singapore);
        configuration.setClientSecret("secret");
        configuration.setClientID("id");
        configuration.setApplicationID(667);
        configuration.setCompanyID(3);

        assertEquals("ProjectID is missing in Configuration Object", true, configuration.hasMissingProperty());
    }

    /*
    * Test Case 8, PSDK-31
    * Given there is a manual configuration object, And the config fields from the config are valid apart from the Application Id which is left null
    * Then there should be an exception pointing out that a mandatory field is empty
    * */
    @Test
    public void ConfigApplicationIdMissing() {

        Configuration configuration = new Configuration();
        assertNotNull(configuration);

        configuration.setRegion(Region.Singapore);
        configuration.setClientSecret("secret");
        configuration.setClientID("id");
        configuration.setProjectID(666);
        configuration.setCompanyID(3);

        assertEquals("ApplicationID is missing in Configuration Object", true, configuration.hasMissingProperty());
    }

    /*
    * Test Case 9, PSDK-31
    * Given there is a manual configuration object, And the config fields from the config are valid apart from the Region which is left null
    * Then there should be an exception pointing out that a mandatory field is empty
    * */
    @Test
    public void ConfigRegionMissing() {

        Configuration configuration = new Configuration();
        assertNotNull(configuration);

        configuration.setClientSecret("secret");
        configuration.setClientID("id");
        configuration.setProjectID(666);
        configuration.setApplicationID(667);
        configuration.setCompanyID(3);

        assertEquals("Region is missing in Configuration Object", true, configuration.hasMissingProperty());
    }

    /*
    * Test Case 10, PSDK-31
    * Given that there is a manual configuration object then Client Id must be a String
    * */
    @Test
    public void ConfigClientIdType() {

        Configuration configuration = new Configuration();
        assertNotNull(configuration);

        configuration.setClientID("id");
        assertEquals("ClientID is not a String", String.class, configuration.getClientID().getClass());
    }

    /*
    * Test Case 11, PSDK-31
    * Given that there is a manual configuration object then Client Secret must be a String
    * */
    @Test
    public void ConfigClientSecretType() {
        Configuration configuration = new Configuration();
        assertNotNull(configuration);

        configuration.setClientSecret("secret");
        assertEquals("ClientSecret is not a String", String.class, configuration.getClientSecret().getClass());
    }

    /*
    * Test Case 12, PSDK-31
    * Given that there is a manual configuration object then Project Id must be an Integer
    * */
    @Test
    public void ConfigProjectIdType() {
        Configuration configuration = new Configuration();
        assertNotNull(configuration);

        configuration.setProjectID(111);
        assertEquals("ClientID is not an Integer", Integer.class, configuration.getProjectID().getClass());
    }

    /*
    * Test Case 13, PSDK-31
    * Given that there is a manual configuration object then Application Id must be an Integer
    * */
    @Test
    public void ConfigApplicationIdType() {
        Configuration configuration = new Configuration();
        assertNotNull(configuration);

        configuration.setApplicationID(111);
        assertEquals("ApplicationID is not an Integer", Integer.class, configuration.getApplicationID().getClass());
    }

    /*
    * Test Case 14, PSDK-31
    * Given that there is a manual configuration object then Region must be a Region type
    * */
    @Test
    public void ConfigRegionType() {
        Configuration configuration = new Configuration();
        assertNotNull(configuration);

        configuration.setRegion(Region.Europe);
        assertEquals("Region is not a Region type", Region.class, configuration.getRegion().getClass());
    }

    /*
    * Test Case 1, PSDK-30
    * Given there is a configuration json file, and the configuration has a Intelligence Region value set to 'US'
    * the SDK will target the related endpoint
    * */
    @Test
    public void JSONFileRegionUrlUS() {

        Configuration configuration = new Configuration();

        try {
            Mockito.when(mockAssetManager.open(Mockito.anyString())).thenReturn(IOUtils.toInputStream("{\"region\":\"US\",\"environment\":\"uat\",\"client_id\":\"AndroidIntelligenceSDKApp_p\",\"client_secret\":\"G1Ep4NU9Tp1Myp726oseqtcmxiopxpxhwzkonyxu\",\"application_id\":\"10069\",\"project_id\":\"40003\",\"company_id\":\"40003\",\"sdk_user_role\":16018,\"certificate_trust_policy\": \"any\"}"));
        } catch (Exception e) {}

        try {
            configuration.loadFromJSON(mockApplication, "intelligence.json");
        } catch (IntelligenceConfigurationException e) {
            fail(e.getMessage());
        }

        assertEquals("Region read from file is not United Stated", configuration.getRegion().getExtension(), Region.UnitedStates.getExtension());
    }

    /*
    * Test Case 2, PSDK-30
    * Given there is a configuration json file, and the configuration has a Intelligence Region value set to 'EU'
    * the SDK will target the related endpoint
    * */
    @Test
    public void JSONFileRegionUrlEU() {
        Configuration configuration = new Configuration();

        try {
            Mockito.when(mockAssetManager.open(Mockito.anyString())).thenReturn(IOUtils.toInputStream("{\"region\":\"EU\",\"environment\":\"uat\",\"client_id\":\"AndroidIntelligenceSDKApp_p\",\"client_secret\":\"G1Ep4NU9Tp1Myp726oseqtcmxiopxpxhwzkonyxu\",\"application_id\":\"10069\",\"project_id\":\"40003\",\"company_id\":\"40003\",\"sdk_user_role\":16018,\"certificate_trust_policy\": \"any\"}"));
        } catch (Exception e) {}

        try {
            configuration.loadFromJSON(mockApplication, "intelligence.json");
        } catch (IntelligenceConfigurationException e) {
            fail(e.getMessage());
        }

        assertEquals("Region read from file is not Europe", configuration.getRegion().getExtension(), Region.Europe.getExtension());
    }

    /*
    * Test Case 3, PSDK-30
    * Given there is a configuration json file, and the configuration has a Intelligence Region value set to 'SG'
    * the SDK will target the related endpoint
    * */
    @Test
    public void JSONFileRegionUrlSG() {
        Configuration configuration = new Configuration();

        try {
            Mockito.when(mockAssetManager.open(Mockito.anyString())).thenReturn(IOUtils.toInputStream("{\"region\":\"SG\",\"environment\":\"uat\",\"client_id\":\"AndroidIntelligenceSDKApp_p\",\"client_secret\":\"G1Ep4NU9Tp1Myp726oseqtcmxiopxpxhwzkonyxu\",\"application_id\":\"10069\",\"project_id\":\"40003\",\"company_id\":\"40003\",\"sdk_user_role\":16018,\"certificate_trust_policy\": \"any\"}"));
        } catch (Exception e) {}

        try {
            configuration.loadFromJSON(mockApplication, "intelligence.json");
        } catch (IntelligenceConfigurationException e) {
            fail(e.getMessage());
        }

        assertEquals("Region read from file is not Singapore", configuration.getRegion().getExtension(), Region.Singapore.getExtension());
    }

    /*
    * Test Case 4, PSDK-30
    * Given there is a configuration json file, and the configuration has a Intelligence Region value set to 'AU'
    * the SDK will target the related endpoint
    * */
    @Test
    public void JSONFileRegionUrlAU() {
        Configuration configuration = new Configuration();

        try {
            Mockito.when(mockAssetManager.open(Mockito.anyString())).thenReturn(IOUtils.toInputStream("{\"region\":\"AU\",\"environment\":\"uat\",\"client_id\":\"AndroidIntelligenceSDKApp_p\",\"client_secret\":\"G1Ep4NU9Tp1Myp726oseqtcmxiopxpxhwzkonyxu\",\"application_id\":\"10069\",\"project_id\":\"40003\",\"company_id\":\"40003\",\"sdk_user_role\":16018,\"certificate_trust_policy\": \"any\"}"));
        } catch (Exception e) {}

        try {
            configuration.loadFromJSON(mockApplication, "intelligence.json");
        } catch (IntelligenceConfigurationException e) {
            fail(e.getMessage());
        }

        assertEquals("Region read from file is not Australia", configuration.getRegion().getExtension(), Region.Australia.getExtension());
    }

    /*
    * Test Case 5, PSDK-30
    * Given there is a configuration json file, and the config fields from the config are valid apart from the Client Id which is left null
    * Then there should be an exception pointing out that a mandatory field is empty
    * */
    @Test
    public void JSONFileClientIdMissing() {

        Configuration configuration = new Configuration();

        try {
            Mockito.when(mockAssetManager.open(Mockito.anyString())).thenReturn(IOUtils.toInputStream("{\"region\":\"EU\",\"environment\":\"uat\",\"client_secret\":\"G1Ep4NU9Tp1Myp726oseqtcmxiopxpxhwzkonyxu\",\"application_id\":\"10069\",\"project_id\":\"40003\",\"company_id\":\"40003\",\"sdk_user_role\":16018}"));
        } catch (Exception e) {}

        //If any of properties is missing, Configuration.loadFromJSON will raise an Exception
        try {
            configuration.loadFromJSON(mockApplication, "intelligence.json");
        } catch (IntelligenceConfigurationException e) {
            return;
        }

        fail("No exception raised that ClientID property is missing");
    }

    /*
    * Test Case 6, PSDK-30
    * Given there is a configuration json file, and the config fields from the config are valid apart from the Client Secret which is left null
    * Then there should be an exception pointing out that a mandatory field is empty
    * */
    @Test
    public void JSONFileClientSecretMissing() {

        Configuration configuration = new Configuration();

        try {
            Mockito.when(mockAssetManager.open(Mockito.anyString())).thenReturn(IOUtils.toInputStream("{\"region\":\"US\",\"environment\":\"uat\",\"client_id\":\"AndroidIntelligenceSDKApp_p\",\"application_id\":\"4154\",\"project_id\":\"2030\",\"company_id\":\"3\",\"sdk_user_role\":16018}"));
        } catch (Exception e) {}

        //If any of properties is missing, Configuration.loadFromJSON will raise an Exception
        try {
            configuration.loadFromJSON(mockApplication, "intelligence.json");
        } catch (IntelligenceConfigurationException e) {
            return;
        }
        fail("No exception raised that ClientSecret property is missing");
    }

    /*
    * Test Case 7, PSDK-30
    * Given there is a configuration json file, and the config fields from the config are valid apart from the Project Id which is left null
    * Then there should be an exception pointing out that a mandatory field is empty
    * */
    @Test
    public void JSONFileProjectIdMissing() {

        Configuration configuration = new Configuration();
        try {
            Mockito.when(mockAssetManager.open(Mockito.anyString())).thenReturn(IOUtils.toInputStream("{\"region\":\"EU\",\"environment\":\"uat\",\"client_id\":\"AndroidIntelligenceSDKApp_p\",\"client_secret\":\"G1Ep4NU9Tp1Myp726oseqtcmxiopxpxhwzkonyxu\",\"application_id\":\"4154\",\"company_id\":\"3\",\"sdk_user_role\":16018}"));
        } catch (Exception e) {}

        //If any of properties is missing, Configuration.loadFromJSON will raise an Exception
        try {
            configuration.loadFromJSON(mockApplication, "intelligence.json");
        } catch (IntelligenceConfigurationException e) {
            return;
        }
        fail("No exception raised that ProjectId property is missing");
    }

    /*
    * Test Case 8, PSDK-30
    * Given there is a configuration json file, and the config fields from the config are valid apart from the Application Id which is left null
    * Then there should be an exception pointing out that a mandatory field is empty
    * */
    @Test
    public void JSONFileApplicationIdMissing() {

        Configuration configuration = new Configuration();
        try {
            Mockito.when(mockAssetManager.open(Mockito.anyString())).thenReturn(IOUtils.toInputStream("{\"region\":\"AU\",\"environment\":\"uat\",\"client_id\":\"AndroidIntelligenceSDKApp_p\",\"client_secret\":\"G1Ep4NU9Tp1Myp726oseqtcmxiopxpxhwzkonyxu\",\"project_id\":\"10069\",\"company_id\":\"19017\",\"sdk_user_role\":16018}"));
        } catch (Exception e) {}

        //If any of properties is missing, Configuration.loadFromJSON will raise an Exception
        try {
            configuration.loadFromJSON(mockApplication, "intelligence.json");
        } catch (IntelligenceConfigurationException e) {
            return;
        }
        fail("No exception raised that ApplicationID property is missing");
    }

    /*
    * Test Case 9, PSDK-30
    * Given there is a configuration json file, and the config fields from the config are valid apart from the Region which is left null
    * Then there should be an exception pointing out that a mandatory field is empty
    * */
    @Test
    public void JSONFileRegionMissing() {

        Configuration configuration = new Configuration();

        try {
            Mockito.when(mockAssetManager.open(Mockito.anyString())).thenReturn(IOUtils.toInputStream("{\"environment\":\"uat\",\"client_id\":\"AndroidIntelligenceSDKApp_p\",\"client_secret\":\"G1Ep4NU9Tp1Myp726oseqtcmxiopxpxhwzkonyxu\",\"application_id\":\"4154\",\"project_id\":\"10069\",\"company_id\":\"19017\",\"sdk_user_role\":16018}"));
        } catch (Exception e) {}

        //If any of properties is missing, Configuration.loadFromJSON will raise an Exception
        try {
            configuration.loadFromJSON(mockApplication, "intelligence.json");
        } catch (IntelligenceConfigurationException e) {
            return;
        }
        fail("No exception raised that Region property is missing");
    }

    /*
    * Test Case 10, PSDK-30
    * Given that there is a configuration json file then Client Id must be a String
    * */
    @Test
    public void JSONFileClientIdType() {

        Configuration configuration = new Configuration();

        try {
            configuration.loadFromJSON(mockApplication, "intelligence.json");
        } catch (IntelligenceConfigurationException e) {
            fail(e.getMessage());
        }

        assertEquals("ClientID read from file is not a String", String.class, configuration.getClientID().getClass());
    }

    /*
    * Test Case 11, PSDK-30
    * Given that there is a configuration json file then Client Secret must be a String
    * */
    @Test
    public void JSONFileClientSecretType() {
        Configuration configuration = new Configuration();

        try {
            configuration.loadFromJSON(mockApplication, "intelligence.json");
        } catch (IntelligenceConfigurationException e) {
            fail(e.getMessage());
        }

        assertEquals("ClientSecret read from file is not a String", String.class, configuration.getClientSecret().getClass());
    }

    /*
    * Test Case 12, PSDK-30
    * Given that there is a configuration json file then Project Id must be an Integer
    * */
    @Test
    public void JSONFileProjectIdType() {
        Configuration configuration = new Configuration();

        try {
            configuration.loadFromJSON(mockApplication, "intelligence.json");
        } catch (IntelligenceConfigurationException e) {
            fail(e.getMessage());
        }

        assertEquals("ProjectID read from file is not an Integer", Integer.class, configuration.getProjectID().getClass());
    }

    /*
    * Test Case 13, PSDK-30
    * Given that there is a configuration json file then Application Id must be an Integer
    * */
    @Test
    public void JSONFileApplicationIdType() {
        Configuration configuration = new Configuration();

        try {
            configuration.loadFromJSON(mockApplication, "intelligence.json");
        } catch (IntelligenceConfigurationException e) {
            fail(e.getMessage());
        }

        assertEquals("ApplicationID read from file is not an Integer", Integer.class, configuration.getApplicationID().getClass());
    }

    /*
    * Test Case 14, PSDK-30
    * Given that there is a configuration json file then Region must be a region type
    * */
    @Test
    public void JSONFileRegionType() {
        Configuration configuration = new Configuration();

        try {
            configuration.loadFromJSON(mockApplication, "intelligence.json");
        } catch (IntelligenceConfigurationException e) {
            fail(e.getMessage());
        }

        assertEquals("Region read from file is not a Region type", Region.class, configuration.getRegion().getClass());
    }

    /*
    * Test Case 2, PSDK-73
    * Given there is a configuration json file, and the config fields from the config are valid apart from the CompanyId which is left null
    * Then there should be an exception pointing out that a mandatory field is empty
    * */
    @Test
    public void JSONFileCompanyIdMissing() {

        Configuration configuration = new Configuration();

        try {
            Mockito.when(mockAssetManager.open(Mockito.anyString())).thenReturn(IOUtils.toInputStream("{\"region\":\"\",\"environment\":\"uat\",\"client_id\":\"ABCDEFG\",\"client_secret\":\"1234567\",\"application_id\":\"1234\",\"project_id\":\"5678\"}"));
        } catch (Exception e) {}

        //If any of properties is missing, Configuration.loadFromJSON will raise an Exception
        try {
            configuration.loadFromJSON(mockApplication, "intelligence.json");
        } catch (IntelligenceConfigurationException e) {
            return;
        }
        fail("No exception raised that CompanyId property is missing");
    }

    /*
    * Given that there is a configuration json file then environment must be a Environment type
    * */
    @Test
    public void JSONFileEnvironmentType() {
        Configuration configuration = new Configuration();

        try {
            configuration.loadFromJSON(mockApplication, "intelligence.json");
        } catch (IntelligenceConfigurationException e) {
            fail(e.getMessage());
        }

        assertEquals("environment read from file is not a Environment type", Environment.class, configuration.getEnvironment().getClass());
    }

    /*
    * Given that there is a configuration json file then certificate_trust_policy must be a CertificateTrustPolicy type
    * */
    @Test
    public void JSONFileCertificateTrustPolicyType() {
        Configuration configuration = new Configuration();

        try {
            configuration.loadFromJSON(mockApplication, "intelligence.json");
        } catch (IntelligenceConfigurationException e) {
            fail(e.getMessage());
        }

        assertEquals("certificate_trust_policy read from file is not a CertificateTrustPolicy type", CertificateTrustPolicy.class, configuration.getCertificateTrustPolicy().getClass());
    }


}
