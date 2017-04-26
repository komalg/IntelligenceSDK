package com.tigerspike.intelligence;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by markvanrees on 03/08/15.
 */
@RunWith(MockitoJUnitRunner.class)
public class RequestTest extends TestCase {

    /*
     * Check if Request constructor creates correct request for specified method, url, params and headers
     */
    @Test
    public void createRequest() {

        URL url = null;

        try {
            url = new URL("https://identity.api.phoenixplatform.com/v2/token");
        } catch (MalformedURLException e) {

        }

        HashMap<String, String> params = new HashMap<>();
        params.put("param1", "param1");
        params.put("param2", "param2");

        HashMap<String, String> headers = new HashMap<>();
        headers.put("header1","header1");
        headers.put("header2", "header2");


        Request request = new Request(Request.Method.POST, url, headers, params);

        assertEquals("Request Method not set correctly", request.getMethod(), Request.Method.POST);

        assertEquals("URL not set correctly", request.getURL().toString(), "https://identity.api.phoenixplatform.com/v2/token");

        HashMap<String, String> params2 = request.getParams();

        assertTrue("Param not set", params2.containsKey("param1"));
        assertTrue("Param not set", params2.containsKey("param2"));
        assertEquals("Param not set correctly", params2.get("param1"), "param1");
        assertEquals("Param not set correctly", params2.get("param2"), "param2");


        HashMap<String, String> headers2 = request.getHeaders();

        assertTrue("Header not set", headers2.containsKey("header1"));
        assertTrue("Header not set", headers2.containsKey("header2"));
        assertEquals("Header not set correctly", headers2.get("header1"), "header1");
        assertEquals("Header not set correctly", headers2.get("header2"), "header2");



    }


}