package com.assignment.core.services;

public interface CryptoService {
    String encryptMessage(String plaintext) throws CryptoException;
    String decryptMessage(String ciphertext) throws CryptoException;
}