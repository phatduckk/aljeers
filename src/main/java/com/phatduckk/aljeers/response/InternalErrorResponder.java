package com.phatduckk.aljeers.response;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class InternalErrorResponder extends Responder {

    public InternalErrorResponder(Throwable responseObject, HttpServletRequest req, HttpServletResponse resp) {
        super(responseObject, req, resp);
    }

    @Override
    protected ResponseStructure getResponseStructure() {
        this.resp.setStatus(500);

        ErrorResponseStructure rs = new ErrorResponseStructure(responseObject);
        rs.setBody(responseObject);
        rs.setStatus(500);

        return rs;
    }
}