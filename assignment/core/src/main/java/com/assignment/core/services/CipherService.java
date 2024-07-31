package com.assignment.core.services;

public interface CipherService {
    public String decrypt(String cipherText, String algorithm) throws Exception;
    public String encrypt(String cipherText, String algorithm) throws Exception;
}
