package com.phatduckk.aljeers.response;

class ErrorResponseStructure extends ResponseStructure {
    protected Throwable exception;

    public ErrorResponseStructure(Object body) {
        super(body);
    }

    public String getExceptionMessage() {
        return ((Throwable) this.body).getMessage();
    }
}