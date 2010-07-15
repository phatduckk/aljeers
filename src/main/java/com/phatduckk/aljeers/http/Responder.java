package com.phatduckk.aljeers.http;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class Responder {
    protected AljeersResponse aljeersResponse;
    protected AljeersRequest req;
    protected HttpServletResponse resp;

    public Responder(Object response, AljeersRequest req, HttpServletResponse resp) {
        this.aljeersResponse = (response instanceof AljeersResponse)
                ? (AljeersResponse) response
                : new AljeersResponse(response);

        this.req = req;
        this.resp = resp;
    }

    public void respond() {
        String json;
        ObjectMapper mapper = null;
        boolean isJsonResponse = !aljeersResponse.isRespondRawString();

        if (isJsonResponse) {
            mapper = new ObjectMapper();
            mapper.configure(SerializationConfig.Feature.WRITE_NULL_PROPERTIES, false);
        }

        if (req.isDebug()) {
            resp.setContentType("text/plain");
            if (isJsonResponse) {
                mapper.configure(SerializationConfig.Feature.INDENT_OUTPUT, true);
            }
        } else {
            String ct = aljeersResponse.getContentType();
            resp.setContentType((ct != null) ? ct : "application/json");
        }

        for (String headerName : aljeersResponse.getHeaders().keySet()) {
            Object headerValue = aljeersResponse.getHeaders().get(headerName);
            resp.setHeader(headerName, (headerValue != null) ? headerValue.toString() : null);
        }

        try {
            resp.setStatus(aljeersResponse.getStatus());
            
            if (aljeersResponse.getBody() != null && aljeersResponse.getStatus() != HttpServletResponse.SC_NO_CONTENT) {
                if (isJsonResponse) {
                    Object serializeMe = (req.isDebug()) ? aljeersResponse : aljeersResponse.getBody();
                    json = mapper.writeValueAsString(serializeMe).trim();
                    resp.getOutputStream().print(json);
                } else {
                    resp.getOutputStream().print(aljeersResponse.getBody().toString());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
