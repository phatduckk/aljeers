package com.phatduckk.aljeers.http;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public class Responder {
    protected AljeersResponse aljeersResponse;
    protected AljeersRequest req;
    protected HttpServletResponse resp;

    public static final String HEADER_DEBUG = "X-Aljeers-Debug";

    public Responder(Object response, AljeersRequest req, HttpServletResponse resp) {
        this.aljeersResponse = (response instanceof AljeersResponse)
                ? (AljeersResponse) response
                : new AljeersResponse(response);

        this.req = req;
        this.resp = resp;
    }

    public void respond() {
        Object serializeMe;
        boolean isDebug = isDebug();
        String json;

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationConfig.Feature.WRITE_NULL_PROPERTIES, false);

        Map<String,Object> headers = aljeersResponse.getHeaders();
        for (String headerName : headers.keySet()) {
            Object headerValue = headers.get(headerName);
            resp.setHeader(headerName, (headerValue != null) ? headerValue.toString() : null);
        }

        resp.setStatus(aljeersResponse.getStatus());

        try {
            if (req.isDebug()) {
                resp.setContentType("text/plain");
                mapper.configure(SerializationConfig.Feature.INDENT_OUTPUT, true);
                serializeMe = aljeersResponse;
            } else {
                resp.setContentType("application/json");
                json = mapper.writeValueAsString(aljeersResponse.getBody()).trim();
                serializeMe = aljeersResponse.getBody();
            }

            if (aljeersResponse.getBody() != null && aljeersResponse.getStatus() != 204) {
                json = mapper.writeValueAsString(serializeMe).trim();
                resp.getOutputStream().print(json);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isDebug() {
        return (req.getHeader(HEADER_DEBUG) != null || req.getParameter("x-aljeers-debug") != null);
    }
}
