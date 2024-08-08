package com.assignment.core.services;

import com.assignment.core.services.exceptions.CipherException;

public interface AsymmetricCipherProvider {
    String getAlgorithm();
    String encrypt(String plaintext) throws CipherException;
    String decrypt(String ciphertext) throws  CipherException;
}