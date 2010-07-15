package com.phatduckk.aljeers.http;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.ObjectMapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;

public class AljeersRequest extends HttpServletRequestWrapper {
    public static final String HEADER_DEBUG = "X-Aljeers-Debug";
    public static final String PARAM_DEBUG = HEADER_DEBUG;

    private byte[] requestBodyBytes;

    public AljeersRequest(HttpServletRequest request) throws IOException {
        super(request);
    }

    public String getBody() {
        return new String(getRawBody());
    }

    public byte[] getRawBody() {
        if (requestBodyBytes == null) {
            try {
                requestBodyBytes = IOUtils.toByteArray(super.getInputStream());
            } catch (IOException e) {
                requestBodyBytes = new byte[0];
            }
        }

        return requestBodyBytes;
    }

    public boolean isDebug() {
        String debugHeader = getHeader(HEADER_DEBUG);
        if (debugHeader != null) {
            return (debugHeader.equals("1") || debugHeader.equals("true"));
        }

        String debugParam = getParameter(PARAM_DEBUG);
        return ((debugParam != null) && (debugParam.equals("1") || debugParam.equals("true")));
    }

    public Object getObjectFromRequest(Class mapToObject) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(getBody(), mapToObject);
    }

    public Object getObjectFromParameter(String parameter, Class mapToObject) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String propertyValue = getParameter(parameter);

        return (propertyValue == null)
                ? null
                : mapper.readValue(propertyValue, mapToObject);
    }
}
