package com.assignment.core.services;

public interface EncryptionService {

    public String encrypt(String message) throws Exception;
    public String decrypt(String encryptedMessage) throws Exception;
}
