package com.tigerspike.intelligence;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

@RunWith(MockitoJUnitRunner.class)
public class RequestBuilderTest extends TestCase {

    /*
     * Test if RequestBuilder correctly build a Request for the given method, url, params and headers.
     */
    @Test
    public void buildRequest() {

        URL url = null;

        try {
            url = new URL("http://www.phoenixplatform.eu");
        } catch (MalformedURLException e) {

        }

        RequestBuilder builder = RequestBuilder.PUT(url)
                .header("header", "header")
                .param("param", "param")
                .accept("text");

        Request request = builder.build();

        assertNotNull("Request not of type Request", request);

        assertNotNull("URL not set",request.getURL());
        assertEquals("URL not set correctly", request.getURL().toString(), "http://www.phoenixplatform.eu");

        assertNotNull("Method not set", request.getMethod());
        assertEquals("Method not set Correctly", request.getMethod(), Request.Method.PUT);

        HashMap<String, String> params = request.getParams();

        assertTrue("Param not set", params.containsKey("param"));
        assertEquals("Param not set correctly", params.get("param"), "param");

        HashMap<String, String> headers = request.getHeaders();

        assertTrue("Accept not set", headers.containsKey("Accept"));
        assertEquals("Accept not set correctly", headers.get("Accept"), "text");

        assertTrue("Header not set", headers.containsKey("header"));
        assertEquals("Header not set correctly", headers.get("header"), "header");

    }

}