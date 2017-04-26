package com.tigerspike.intelligence;

/**
 * Created by marcinowoc on 11/02/16.
 */
public enum HTTPStatusCode{
    SUCCESS(200),
    BAD_REQUEST(400),
    UNAUTHORIZED(401),
    FORBIDDEN(403),
    NOT_FOUND(404);

    private Integer statusCode;

    HTTPStatusCode(Integer code) {
        statusCode = code;
    }

    public Integer getStatusCode(){
        return statusCode;
    }
}
