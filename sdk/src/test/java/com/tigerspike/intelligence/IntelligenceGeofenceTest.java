package com.tigerspike.intelligence;

import junit.framework.TestCase;

import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;

@RunWith(MockitoJUnitRunner.class)
public class IntelligenceGeofenceTest extends TestCase
{
    /*
    * Tests if JSON file is read correctly and all parameters are set as expected
    * */
    @Test
    public void ReadingJson() throws IOException, JSONException{
        String geofenceJson = "{\"Id\": \"67745\", \"ProjectId\": \"40003\", \"Name\": \"Surry Hills Suburb\", \"Address\": \"119 Devonshire St, Surry Hills NSW 2010, Australia\", \"Geolocation\": {\"Latitude\": \"-33.8870009\", \"Longitude\": \"151.2088037\"}, \"Radius\": \"528.23\", \"Tags\": \"tags\", \"DateCreated\": \"2016-01-05T04:55:43.83Z\", \"DateUpdated\": \"2016-01-05T04:55:43.83Z\"}";

        IntelligenceGeofence geofence = new IntelligenceGeofence(geofenceJson);

        assertEquals("Id not match", new Integer(67745), geofence.getId());
        assertEquals("Project Id not match", new Integer(40003), geofence.getProjectId());
        assertEquals("name not match", "Surry Hills Suburb", geofence.getName());
        assertEquals("Address not match", "119 Devonshire St, Surry Hills NSW 2010, Australia", geofence.getAddress());
        assertEquals("Latitude not match", -33.8870009, geofence.getLatitude());
        assertEquals("Longitude not match", 151.2088037, geofence.getLongitude());
        assertEquals("Radius not match", 528.23, geofence.getRadius());
        assertEquals("Tags not match", "tags", geofence.getTags());
    }
}
