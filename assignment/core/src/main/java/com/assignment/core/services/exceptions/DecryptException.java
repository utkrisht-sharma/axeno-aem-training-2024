package com.assignment.core.services.exceptions;

public class DecryptException extends CryptoException  {
    public DecryptException(String message, Throwable cause) {
        super(message, cause);
    }
}