package com.phatduckk.aljeers.handler;

import com.phatduckk.aljeers.exception.InvalidRequestException;
import com.phatduckk.aljeers.http.Responder;
import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.ObjectMapper;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public abstract class BaseHandler extends HttpServlet {
    private static final String DEFAULT_ENDPOINT = "index";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        try {
            respond(req, resp);
        } catch (Throwable throwable) {
            respond(throwable, req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        try {
            respond(req, resp);
        } catch (Throwable throwable) {
            respond(throwable, req, resp);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
        try {
            respond(req, resp);
        } catch (Throwable throwable) {
            respond(throwable, req, resp);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        try {
            respond(req, resp);
        } catch (Throwable throwable) {
            respond(throwable, req, resp);
        }
    }

    @Override
    protected void doHead(HttpServletRequest req, HttpServletResponse resp) {
        try {
            respond(req, resp);
        } catch (Throwable throwable) {
            respond(throwable, req, resp);
        }
    }

    private void respond(HttpServletRequest req, HttpServletResponse resp) {
        String[] uriParts = req.getRequestURI().split("/");
        String endpointName = DEFAULT_ENDPOINT;

        if (uriParts.length > 2) {
            endpointName = uriParts[2];
        }

        Class[] args = new Class[2];
        args[0] = HttpServletRequest.class;
        args[1] = HttpServletResponse.class;

        try {
            Method method = getEndpointMethod(endpointName, args);
            validateHttpMethod(req, method);
            respond(method.invoke(this, req, resp), req, resp);
        } catch (InvalidRequestException e) {
            respond(new InvalidRequestException("Invalid endpoint: " + endpointName), req, resp);
        } catch (NoSuchMethodException e) {
            respond(new InvalidRequestException("Invalid endpoint: " + endpointName), req, resp);
        } catch (InvocationTargetException e) {
            respond(e.getTargetException(), req, resp);
        } catch (Throwable e) {
            respond(e, req, resp);
        }
    }

    private Method getEndpointMethod(String endpointName, Class[] args) throws NoSuchMethodException {
        return this.getClass().getMethod(endpointName, args);
    }

    private void validateHttpMethod(HttpServletRequest req, Method method) throws InvalidRequestException {
        String httpRequestMethod = req.getMethod();
        Annotation[] annotations = method.getAnnotations();

        for (Annotation annotation : annotations) {
            if (httpRequestMethod.equals(annotation.annotationType().getSimpleName())) {
                return;
            }
        }

        throw new InvalidRequestException("Cannot access " + method.getName() + " via HTTP " + httpRequestMethod);
    }

    private void respond(Object responseObject, HttpServletRequest req, HttpServletResponse resp) {
        Responder responder = new Responder(responseObject, req, resp);
        responder.respond();
    }

    protected String getRequestBody(HttpServletRequest req) throws IOException {
        String s = new String(IOUtils.toByteArray(req.getInputStream()));
        return s;
    }

    protected Object getObjectFromRequest(HttpServletRequest req, Class mapToObject) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(getRequestBody(req), mapToObject);
    }

    protected Object getObjectFromRequestParameter(HttpServletRequest req, String parameter, Class mapToObject) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String propertyValue = req.getParameter(parameter);

        return (propertyValue == null)
                ? null
                : mapper.readValue(propertyValue, mapToObject);
    }
}