package com.azure.spring.initializr.extension.scm.push.common.exception;

public class OAuthAppException extends RuntimeException {
    private static final long serialVersionUID = -5365631128956061164L;

    public OAuthAppException() {
        super();
    }

    public OAuthAppException(String s) {
        super(s);
    }

    public OAuthAppException(String message, Throwable cause) {
        super(message, cause);
    }

    public OAuthAppException(Throwable cause) {
        super(cause);
    }
}
