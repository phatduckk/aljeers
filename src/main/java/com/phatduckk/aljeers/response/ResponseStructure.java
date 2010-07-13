package com.phatduckk.aljeers.response;

public class ResponseStructure {
    protected Object body;
    protected int status = 200;
    protected String responseType;

    public ResponseStructure(Object body) {
        this.body = body;
    }

    public String getResponseType() {
        return (body != null)
                ? body.getClass().getCanonicalName()
                : null;
    }

    public String getSimpleResponseType() {
        return (body != null)
                ? body.getClass().getSimpleName()
                : null;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }
}
