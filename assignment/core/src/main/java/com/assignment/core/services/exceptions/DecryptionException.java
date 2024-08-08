package com.assignment.core.services.exceptions;

public class DecryptionException extends CipherException {
    public DecryptionException(String message, Throwable cause) {
        super(message, cause);
    }
}
