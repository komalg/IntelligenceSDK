package com.tigerspike.intelligence;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Created by markvanrees on 03/08/15.
 */
@RunWith(MockitoJUnitRunner.class)
public class InstallationTest extends TestCase {

    private Application mockApplication;

    @Before
    public void init() {

        PackageInfo packageInfo = Mockito.mock(PackageInfo.class);
        packageInfo.versionName = "1.0";

        PackageManager packageManager = Mockito.mock(PackageManager.class);
        try {
            Mockito.when(packageManager.getPackageInfo(Mockito.anyString(), Mockito.anyInt())).thenReturn(packageInfo);
        } catch (PackageManager.NameNotFoundException e) {}

        mockApplication = Mockito.mock(Application.class);
        Mockito.when(mockApplication.getPackageName()).thenReturn("com.tigerspike.intelligence");
        Mockito.when(mockApplication.getPackageManager()).thenReturn(packageManager);

    }

    /*
     * Test creation of Installation object through available app properties.
     */
    @Test
    public void createInstallationObjectFromContext() {

        Installation installation = null;

        try {
            installation = new Installation(mockApplication);
        } catch (Exception e) {
            fail("Could not create Installation object from Application context");
        }

        assertNotNull("Could not create Installation object from Application context");

        assertEquals("InstalledVersion not read properly", "1.0", installation.getInstalledVersion());
        assertEquals("OperatingSystemVersion not read properly", "5.1", installation.getOperatingSystemVersion());

        assertEquals("DeviceTypeID not set properly", Installation.DeviceType.SMARTPHONE.getValue(), installation.getDeviceTypeID());

        assertNull("UserID is not Null", installation.getUserID());
        assertNull("ProjectID is not Null", installation.getProjectID());
        assertNull("ApplicationID is not Null", installation.getApplicationID());

        assertNull("ID is not Null", installation.getID());
        assertNull("DateCreated is not Null", installation.getCreateDate());
        assertNull("DateUpdated is not Null", installation.getUpdateDate());

    }

    /*
      * Test exporting a Installation to a json string and creating a Installation object from json.
      */
    @Test
    public void createInstallationFromJSON() {

        Installation installation = null;

        try {
            installation = new Installation(mockApplication);
        } catch (Exception e) {
            fail("Could not create Installation object from Application context");
        }

        String jsonInstallation = null;

        try {
            jsonInstallation = installation.toJSONString();
        } catch (Exception e) {
            fail("Error while convering installation to json");
        }

        if (jsonInstallation == null) {
            fail("Error while convering installation to json");
        }

        Installation installationFromJson = null;

        try {
            installationFromJson = new Installation(jsonInstallation);
        } catch (Exception e) {
            fail("Error while creating installation from json");
        }

        if (installationFromJson == null) {
            fail("Could not create installation from json");
        }

        assertEquals("InstalledVersion not read properly", installationFromJson.getInstalledVersion(), installation.getInstalledVersion());
        assertEquals("OperatingSystemVersion not read properly", installationFromJson.getOperatingSystemVersion(), installation.getOperatingSystemVersion());

        assertEquals("InstallationID not set properly", installationFromJson.getInstallationID(), installation.getInstallationID());
        assertEquals("ApplicationID not set properly ", installationFromJson.getApplicationID(), installation.getApplicationID());


    }


}