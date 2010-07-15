package com.phatduckk.aljeers.handler;

import com.phatduckk.aljeers.exception.MethodNotAllowedException;
import com.phatduckk.aljeers.exception.NotFoundException;
import com.phatduckk.aljeers.http.AljeersRequest;
import com.phatduckk.aljeers.http.AljeersResponse;
import com.phatduckk.aljeers.http.Responder;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
        AljeersRequest aljeersReq = (AljeersRequest) req;
        String[] uriParts = aljeersReq.getRequestURI().split("/");
        String endpointName = DEFAULT_ENDPOINT;

        if (uriParts.length > 2) {
            endpointName = uriParts[2];
        }

        Class[] args = new Class[2];
        args[0] = AljeersRequest.class;
        args[1] = HttpServletResponse.class;

        try {
            Method method = getEndpointMethod(endpointName, args);
            validateHttpMethod(aljeersReq, method);
            Object responseObject = method.invoke(this, aljeersReq, resp);
            respond(responseObject, aljeersReq, resp);
        } catch (MethodNotAllowedException e) {
            respond(e, aljeersReq, resp);
        } catch (NoSuchMethodException e) {
            respond(new NotFoundException("Invalid endpoint: " + endpointName), aljeersReq, resp);
        } catch (InvocationTargetException e) {
            respond(e.getTargetException(), aljeersReq, resp);
        } catch (Throwable e) {
            respond(e, aljeersReq, resp);
        }
    }

    private Method getEndpointMethod(String endpointName, Class[] args) throws NoSuchMethodException {
        return this.getClass().getMethod(endpointName, args);
    }

    private void validateHttpMethod(HttpServletRequest req, Method method) throws MethodNotAllowedException {
        String httpRequestMethod = req.getMethod();
        Annotation[] annotations = method.getAnnotations();

        for (Annotation annotation : annotations) {
            if (httpRequestMethod.equals(annotation.annotationType().getSimpleName())) {
                return;
            }
        }

        throw new MethodNotAllowedException("Cannot access " + method.getName() + " via HTTP " + httpRequestMethod);
    }

    private void respond(Object responseObject, HttpServletRequest req, HttpServletResponse resp) {
        Responder responder = (responseObject instanceof AljeersResponse)
                ? new Responder((AljeersResponse) responseObject, (AljeersRequest) req, resp)
                : new Responder(responseObject, (AljeersRequest) req, resp);

        responder.respond();
    }
}