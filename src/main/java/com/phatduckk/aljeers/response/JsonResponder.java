package com.phatduckk.aljeers.response;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class JsonResponder extends Responder {
    public JsonResponder(Object responseObject, HttpServletRequest req, HttpServletResponse resp) {
        super(responseObject, req, resp);
    }

    @Override
    protected ResponseStructure getResponseStructure() {
        this.resp.setStatus(200);

        ResponseStructure rs = new ResponseStructure(this.responseObject);
        rs.setBody(this.responseObject);

        return rs;
    }
}
