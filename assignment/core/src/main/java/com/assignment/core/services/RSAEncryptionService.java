package com.assignment.core.services;

public interface RSAEncryptionService {
    public String encrypt(String message) throws Exception;
    public String decrypt(String encryptedMessage) throws Exception;
}

