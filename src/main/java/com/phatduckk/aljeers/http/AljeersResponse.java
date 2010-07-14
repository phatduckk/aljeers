package com.phatduckk.aljeers.http;

import java.util.HashMap;
import java.util.Map;

/**
 * User: arin
 * Date: Jul 14, 2010
 * Time: 1:11:14 PM
 */


public class AljeersResponse {
    private Object body = null;
    private int status = 200;
    private Map<String, Object> headers = new HashMap<String, Object>();

    private static final String HEADER_STATUS = "X-Aljeers-Status";
    private static final String HEADER_BODY_TYPE = "X-Aljeers-BodyType";
    private static final String HEADER_BODY_TYPE_SIMPLE = "X-Aljeers-BodyType-Simple";

    public AljeersResponse(Object body) {
        this.body = body;
    }

    public AljeersResponse(Object body, int status, Map<String, Object> headers) {
        setBody(body);
        setStatus(status);
        this.headers = headers;
    }

    public AljeersResponse(Object body, Map<String, Object> headers) {
        setBody(body);
        setStatus(status);
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        addHeader(HEADER_BODY_TYPE, body.getClass().getCanonicalName());
        addHeader(HEADER_BODY_TYPE_SIMPLE, body.getClass().getSimpleName());

        if (body == null) {
            setStatus(204);
        } else if (body instanceof Throwable) {
            setStatus(500);
        } else {
            setStatus(200);
        }

        this.body = body;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        addHeader(HEADER_STATUS, status);
        this.status = status;
    }

    private void addHeader(String headerName, Object headerValue) {
        headers.put(headerName, headerValue);
    }

    public Map<String, Object> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, Object> headers) {
        this.headers = headers;
    }
}
