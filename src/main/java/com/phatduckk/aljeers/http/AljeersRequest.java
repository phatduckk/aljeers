package com.phatduckk.aljeers.http;

import org.apache.commons.io.IOUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;

public class AljeersRequest extends HttpServletRequestWrapper {
    public static final String HEADER_DEBUG = "X-Aljeers-Debug";
    public static final String PARAM_DEBUG = HEADER_DEBUG;

    private byte[] requestBodyBytes;

    public AljeersRequest(HttpServletRequest request) throws IOException {
        super(request);
        requestBodyBytes = IOUtils.toByteArray(super.getInputStream());
    }

    public String getBody() {
        return new String(requestBodyBytes);
    }

    public byte[] getRawBody() {
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
}
