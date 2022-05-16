package com.azure.spring.initializr.extension.connector.common.exception;

public class ConnectorException extends RuntimeException{
    private static final long serialVersionUID = -5365631128956061164L;

    public ConnectorException() {
        super();
    }

    public ConnectorException(String s) {
        super(s);
    }

    public ConnectorException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConnectorException(Throwable cause) {
        super(cause);
    }
}
