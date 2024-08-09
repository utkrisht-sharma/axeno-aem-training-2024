package com.assignment.core.exceptions;

public class ProviderNotFoundException extends Exception {

    public ProviderNotFoundException(String message) {
        super(message);
    }
    public ProviderNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
