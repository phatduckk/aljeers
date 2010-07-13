package com.phatduckk.aljeers.response;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public abstract class Response {
    protected Object responseObject;
    protected HttpServletRequest req;
    protected HttpServletResponse resp;

    public static final String HEADER_DEBUG = "X-Aljeers-Debug";
    public static final String HEADER_EXCEPTION_MESSAGE = "X-Aljeers-Exception-Message";
    public static final String HEADER_RESPONSE_BODY_TYPE = "X-Aljeers-Exception-Type";
    public static final String HEADER_RESPONSE_BODY_TYPE_SIMPLE = "X-Aljeers-Exception-Type-Simple";
    public static final String HEADER_STATUS = "X-Aljeers-Status";

    protected Response(Object responseObject, HttpServletRequest req, HttpServletResponse resp) {
        this.responseObject = responseObject;
        this.req = req;
        this.resp = resp;
    }

    public static Response factory(Object responseObject, HttpServletRequest req, HttpServletResponse resp) {
        if (responseObject instanceof Throwable) {
            return new InternalErrorResponse((Throwable) responseObject, req, resp);
        } else {
            return new JsonResponse(responseObject, req, resp);
        }
    }

    public void respond() {
        ResponseStructure struct = getResponseStructure();
        boolean isDebug = isDebug();
        String json;

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationConfig.Feature.WRITE_NULL_PROPERTIES, false);

        resp.setHeader(HEADER_STATUS, Integer.toString(struct.getStatus()));
        resp.setHeader(HEADER_RESPONSE_BODY_TYPE, struct.getResponseType());
        resp.setHeader(HEADER_RESPONSE_BODY_TYPE_SIMPLE, struct.getSimpleResponseType());

        if (struct instanceof ErrorResponseStructure) {
            ErrorResponseStructure errorStruct = (ErrorResponseStructure) struct;
            resp.setHeader(HEADER_EXCEPTION_MESSAGE, errorStruct.getExceptionMessage());
        }

        try {
            if (isDebug) {
                resp.setContentType("text/plain");
                resp.setHeader(HEADER_DEBUG, "true");
                mapper.configure(SerializationConfig.Feature.INDENT_OUTPUT, true);
                json = mapper.writeValueAsString(struct).trim();
            } else {
                resp.setContentType("application/json");
                resp.setHeader(HEADER_DEBUG, "false");
                json = mapper.writeValueAsString(struct.getBody()).trim();
            }

            resp.getOutputStream().print(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isDebug() {
        return (req.getHeader(HEADER_DEBUG) != null || req.getParameter("x-aljeers-debug") != null);
    }

    protected abstract ResponseStructure getResponseStructure();
}
