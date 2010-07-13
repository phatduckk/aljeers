package com.phatduckk.aljeers.request;

import org.apache.commons.io.IOUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;

public class JsonRequest extends HttpServletRequestWrapper {
    private byte[] requestBodyBytes;

    public JsonRequest(HttpServletRequest request) throws IOException {
        super(request);

        requestBodyBytes = IOUtils.toByteArray(super.getInputStream());
        super.getInputStream().reset();
        System.out.println("jhuh");
    }

    public String getBody() {
        return new String(requestBodyBytes);
    }

    public byte[] getRawBody() {
        return requestBodyBytes;
    }
}
