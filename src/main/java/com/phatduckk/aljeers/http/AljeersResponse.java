package com.phatduckk.aljeers.http;

import com.phatduckk.aljeers.exception.MethodNotAllowedException;
import com.phatduckk.aljeers.exception.NotFoundException;
import org.codehaus.jackson.annotate.JsonIgnore;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

public class AljeersResponse {
    private Object body = null;
    private int status = HttpServletResponse.SC_OK;
    private Map<String, Object> headers = new HashMap<String, Object>();

    private static final String HEADER_STATUS = "X-Aljeers-Status";
    private static final String HEADER_BODY_TYPE = "X-Aljeers-Body-Type";
    private static final String HEADER_BODY_TYPE_SIMPLE = "X-Aljeers-Body-Type-Simple";
    private static final String HEADER_EXCEPTION_MESSAGE = "X-Aljeers-Exception-Message";

    public AljeersResponse(Object body) {
        setBody(body);
    }

    public AljeersResponse(Object body, int status, Map<String, Object> headers) {
        setBody(body);
        setStatus(status);
        setHeaders(headers);
    }

    public AljeersResponse(Object body, Map<String, Object> headers) {
        setBody(body);
        setHeaders(headers);
    }

    public AljeersResponse() {

    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;

        addHeader(HEADER_BODY_TYPE, (body != null) ? body.getClass().getCanonicalName() : null);
        addHeader(HEADER_BODY_TYPE_SIMPLE, (body != null) ? body.getClass().getSimpleName() : null);

        if (body == null) {
            setStatus(HttpServletResponse.SC_NO_CONTENT);
        } else if (isBodyThrowable()) {
            if (body instanceof NotFoundException) {
                setStatus(HttpServletResponse.SC_NOT_FOUND);
            } else if (body instanceof MethodNotAllowedException) {
                setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            } else {
                setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } else {
            setStatus(HttpServletResponse.SC_OK);
        }
    }

    @JsonIgnore
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        setStatus(status, true);
    }

    public void setStatus(int status, boolean isSetMessageHeaderOnThrowable) {
        this.status = status;
        boolean isBodyThrowable = isBodyThrowable();

        addHeader(HEADER_STATUS, status);

        if (isSetMessageHeaderOnThrowable && isBodyThrowable) {
            addHeader(HEADER_EXCEPTION_MESSAGE, ((Throwable) body).getMessage());
        }

        if (!isBodyThrowable) {
            removeHeader(HEADER_EXCEPTION_MESSAGE);
        }
    }

    public void addHeader(String headerName, Object headerValue) {
        headers.put(headerName, headerValue);
    }

    public void addHeaders(Map<String, Object> headers) {
        headers.putAll(headers);
    }

    public Map<String, Object> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, Object> headers) {
        headers = headers;
    }

    public void removeHeader(String header) {
        headers.remove(header);
    }

    @JsonIgnore
    public boolean isBodyThrowable() {
        return body instanceof Throwable;
    }
}
