package com.azure.spring.initializr.extension.scm.common.exception;

public class SCMException extends RuntimeException{
    private static final long serialVersionUID = -5365631128956061164L;

    public SCMException() {
        super();
    }

    public SCMException(String s) {
        super(s);
    }

    public SCMException(String message, Throwable cause) {
        super(message, cause);
    }

    public SCMException(Throwable cause) {
        super(cause);
    }
}
