package com.assignment.core.services;

import com.assignment.core.exceptions.EncryptionException;
import com.assignment.core.exceptions.ProviderNotFoundException;

public interface CipherService {
    public String decrypt(String cipherText, String algorithm) throws EncryptionException, ProviderNotFoundException;
    public String encrypt(String cipherText, String algorithm) throws EncryptionException, ProviderNotFoundException;
}
