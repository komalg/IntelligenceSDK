package com.tigerspike.intelligence;

import junit.framework.TestCase;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;

/**
 * Created by o on 2016-02-09.
 */
@RunWith(MockitoJUnitRunner.class)
public class AnalyticsEventTest  extends TestCase {

    /*
    * Check if AnalyticsEvent is created properly
    */
    @Test
    public void createAnalyticsEvent() throws JSONException{
        AnalyticsEvent analyticsEvent = new AnalyticsEvent("up","300");

        analyticsEvent.setIpAddress("192.168.0.1");
        analyticsEvent.setInstallationID("installationId");
        analyticsEvent.setApplicationVersion("0.2.0");
        analyticsEvent.setDeviceType("HUAWEI P7-L10");
        analyticsEvent.setOperatingSystem("Android 4.4.2");
        analyticsEvent.setApplicationID(10069);
        analyticsEvent.setProjectId(40003);
        analyticsEvent.setUserID(310375);
        analyticsEvent.setLocation(37.332331, -122.031219);
        HashMap<String, String> metadata = new HashMap<String, String>();
        metadata.put("key", "value");
        analyticsEvent.setMetaData(metadata);

        JSONObject jsonObject = analyticsEvent.toJSONObject();

        assertNotNull("Date not set", jsonObject.getString("EventDate"));
        assertEquals("ProjectId not match", 40003, jsonObject.getInt("ProjectId"));
        assertEquals("IpAddress not match", "192.168.0.1", jsonObject.getString("IpAddress"));
        assertEquals("EventType not match", "up", jsonObject.getString("EventType"));
        assertEquals("EventValue not match", 300d, jsonObject.getDouble("EventValue"));
        assertEquals("PhoenixIdentity_UserId not match", 310375, jsonObject.getInt("PhoenixIdentity_UserId"));
        assertEquals("PhoenixIdentity_ApplicationId not match", 10069, jsonObject.getInt("PhoenixIdentity_ApplicationId"));
        assertEquals("PhoenixIdentity_InstallationId not match", "installationId", jsonObject.getString("PhoenixIdentity_InstallationId"));
        assertEquals("ApplicationVersion not match", "0.2.0", jsonObject.getString("ApplicationVersion"));
        assertEquals("DeviceType not match", "HUAWEI P7-L10", jsonObject.getString("DeviceType"));
        assertEquals("OperatingSystemVersion not match", "Android 4.4.2", jsonObject.getString("OperatingSystemVersion"));
        assertEquals("Longitude not match", 37.332331, jsonObject.getJSONObject("Geolocation").getDouble("Longitude"));
        assertEquals("Latitude not match", -122.031219, jsonObject.getJSONObject("Geolocation").getDouble("Latitude"));
        assertEquals("MetaData not match", "{\"key\":\"value\"}", jsonObject.getJSONObject("MetaData").toString());
    }
}
