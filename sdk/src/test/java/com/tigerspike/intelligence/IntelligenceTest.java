package com.tigerspike.intelligence;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;

import com.tigerspike.intelligence.exceptions.IntelligenceConfigurationException;

import junit.framework.TestCase;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import mock.mockSharedPreferences;

/**
 * Created by Mel on 23/07/15.
 */
@RunWith(MockitoJUnitRunner.class)
public class IntelligenceTest extends TestCase {

    public AssetManager mockAssetManager;
    public Application mockApplication;

    /*
    * Initializes mock objects needed to perform Configuration tests
    * */
    @Before
    public void init() {

        // Mock Build Variables.

        PackageInfo packageInfo = Mockito.mock(PackageInfo.class);
        packageInfo.versionName = "1.0";

        // Mock PackageManager
        PackageManager packageManager = Mockito.mock(PackageManager.class);
        try {
            Mockito.when(packageManager.getPackageInfo(Mockito.anyString(), Mockito.anyInt())).thenReturn(packageInfo);
        } catch (PackageManager.NameNotFoundException e) {}



        // Mock Asset Manager
        mockAssetManager = Mockito.mock(AssetManager.class);
        try {
            Mockito.when(mockAssetManager.open(Mockito.anyString())).thenReturn(IOUtils.toInputStream("{\"region\":\"US\",\"client_id\":\"ABCDEFG\",\"client_secret\":\"1234567\",\"application_id\":\"1234\",\"project_id\":\"5678\",\"company_id\":333}"));
        } catch (Exception e) {}

        // Create Application Mock.
        mockApplication = Mockito.mock(Application.class);
        Mockito.when(mockApplication.getAssets()).thenReturn(mockAssetManager);
        Mockito.when(mockApplication.getPackageName()).thenReturn("com.tigerspike.intelligence");
        Mockito.when(mockApplication.getPackageManager()).thenReturn(packageManager);

        // Mock Shared Preferences
        SharedPreferences sharedPreferences = new mockSharedPreferences(mockApplication);

        Mockito.when(mockApplication.getSharedPreferences(Mockito.anyString(), Mockito.anyInt())).thenReturn(sharedPreferences);

    }

    /*
     * Checks if Intelligence constructor succesfully creates an intelligence instance from provided application refrence and json filename.
     * TODO Fix test
     */
//    @Test
//    public void createIntelligenceWithJSON() {
//
//        try {
//            Mockito.when(mockAssetManager.open(Mockito.anyString())).thenReturn(IOUtils.toInputStream("{\"region\":\"US\",\"client_id\":\"AndroidSDKApp_kypopf\",\"client_secret\":\"OCN20qleymuqjlqbfcwbcwnjdwphdgoxpocrpxvp\",\"application_id\":\"4154\",\"project_id\":\"2030\",\"company_id\":\"3\",\"sdk_user_role\":1008}"));
//        } catch (Exception e) {
//        }
//
//        try {
//            Intelligence intelligence = new Intelligence(mockApplication, "intelligence.json");
//        } catch (IntelligenceConfigurationException e) {
//            fail("Failed creating Intelligence instance with json");
//        }
//
//    }

    /*
     * Checks if Intelligence constructor successfully creates an intelligence instance from provided application reference and configuration object.
     * TODO Fix test
     */
//    @Test
//    public void createIntelligenceWithManualConfiguration() {
//
//        try {
//            Configuration configuration = new Configuration();
//            configuration.setRegion(Region.Singapore);
//            configuration.setClientID("clientid");
//            configuration.setClientSecret("secret");
//            configuration.setProjectID(666);
//            configuration.setApplicationID(667);
//            configuration.setCompanyID(33);
//            configuration.setSDKUserRole(1008);
//
//            Intelligence intelligence = new Intelligence(mockApplication, configuration);
//        } catch (IntelligenceConfigurationException e) {
//            fail("Failed creating Intelligence instance manually");
//        }
//
//    }

    /*
     * Check if Intelligence constructor throws error when trying to initialize with incomplete json string.
     * A json string which does not contain all required properties.
     */
    @Test
    public void createIntelligenceWithIncompleteJSONConfiguration() {

        try {
            Mockito.when(mockAssetManager.open(Mockito.anyString())).thenReturn(IOUtils.toInputStream("{\"application_id\":\"1234\",\"project_id\":\"5678\"}"));
        } catch (Exception e) {
        }

        try {
            Configuration configuration = new Configuration();
            configuration.setRegion(Region.Singapore);
            configuration.setClientID("clientid");
            configuration.setApplicationID(667);

            Intelligence intelligence = new Intelligence(mockApplication, "intelligence.json");
        } catch (IntelligenceConfigurationException e) {
            return;
        }

        fail("No error thrown for missing parameters in configuration");

    }

    /*
     * Check if Intelligence constructor throws correct IntelligenceConfigurationError when trying to initialize with a invalid json string
     */
    @Test
    public void createIntelligenceWithEronousJSONConfiguration() {

        try {
            Mockito.when(mockAssetManager.open(Mockito.anyString())).thenReturn(IOUtils.toInputStream("{\"application_id1234\",\"project_id\":\"5678\"}"));
        } catch (Exception e) {
        }

        try {
            Configuration configuration = new Configuration();
            configuration.setRegion(Region.Singapore);
            configuration.setClientID("clientid");
            configuration.setApplicationID(667);

            Intelligence intelligence = new Intelligence(mockApplication, "intelligence.json");
        } catch (IntelligenceConfigurationException e) {
            return;
        }

        fail("No error thrown for faulty configuration");

    }

    /*
     * Checks if Intelligence constructor throws correct IntelligenceConfigurationError when trying to initialize with an incomplete manual Configuration object.
     */
    @Test
    public void createIntelligenceWithIncompleteManualConfiguration() {

        try {
            Configuration configuration = new Configuration();
            configuration.setRegion(Region.Singapore);
            configuration.setClientID("clientid");
            configuration.setApplicationID(667);

            Intelligence intelligence = new Intelligence(mockApplication, configuration);
        } catch (IntelligenceConfigurationException e) {
            return;
        }

        fail("No error thrown for missing parameters in configuration");

    }

}