package com.assignment.core.services.exceptions;

public class EncryptionException extends CipherException {
    public EncryptionException(String message, Throwable cause) {
        super(message, cause);
    }
}
