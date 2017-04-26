package com.tigerspike.intelligence;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Created by markvanrees on 03/08/15.
 */
@RunWith(MockitoJUnitRunner.class)
public class ResponseTest extends TestCase {

    /*
     * Check if Response constructor creates correct response object for provided request, code, body data and exception
     */
    @Test
    public void createResponse() {

        Request request = Mockito.mock(Request.class);
        Exception exception = Mockito.mock(Exception.class);

        Response response = new Response(request,200,"body",exception);

        assertNotNull("Request is missing", response.request());
        assertEquals("Request is incorrect", request, response.request());

        assertEquals("Response code incorrect", 200, response.code());

        assertTrue("Body is incorrect", response.bodyData().contentEquals("body"));

        assertNotNull("Exception is missing", response.exception());

    }


}