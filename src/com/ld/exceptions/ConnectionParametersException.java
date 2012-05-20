package com.ld.exceptions;

public class ConnectionParametersException extends Exception {
    
    private static final long serialVersionUID = 1L;
    public static String WRONG_PORT_MESSAGE = "Port must be non-negative integer";

    public ConnectionParametersException(String message) {
        super(message);
    }
}
