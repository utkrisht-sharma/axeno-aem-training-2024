package com.assignment.core.services;

import com.assignment.core.exceptions.EncryptionException;

public interface CipherService {
    public String decrypt(String cipherText, String algorithm) throws EncryptionException;
    public String encrypt(String cipherText, String algorithm) throws EncryptionException;
}
