package com.assignment.core.services;

import com.assignment.core.services.exceptions.CryptoException;

public interface CryptoService {
    String encryptMessage(String plaintext) throws CryptoException;
    String decryptMessage(String ciphertext) throws CryptoException;
}