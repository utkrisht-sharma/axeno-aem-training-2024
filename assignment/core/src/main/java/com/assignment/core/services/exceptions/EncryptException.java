package com.assignment.core.services.exceptions;

public class EncryptException extends CryptoException  {
    public EncryptException(String message, Throwable cause) {
        super(message, cause);
    }
}