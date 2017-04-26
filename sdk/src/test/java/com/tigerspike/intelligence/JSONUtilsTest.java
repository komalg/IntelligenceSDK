package com.tigerspike.intelligence;

import junit.framework.TestCase;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Created by markvanrees on 03/08/15.
 */
@RunWith(MockitoJUnitRunner.class)
public class JSONUtilsTest extends TestCase {

    /*
     * Check if parseSimpleJSONObject correctly parses json string into a JSONObject
     */
    @Test
    public void parseSimpleJSONObject() {

        JSONObject jsonObject = null;

        try {
            jsonObject = JSONUtils.parseSimpleJSONObject("{\"name\":\"John\",\"age\":25}");

            assertTrue("name missing",jsonObject.has("name"));
            assertTrue("name incorrect", jsonObject.getString("name").equals("John"));
            assertTrue("age missing",jsonObject.has("age"));
            assertTrue("age incorrect",jsonObject.getInt("age") == 25);

        } catch (JSONException e) {
            fail("Error parsing json");
        }




    }


}