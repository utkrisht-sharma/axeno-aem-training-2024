package com.assignment.core.services;

import com.assignment.core.exceptions.EncryptionException;

public interface AsymmetricCipherProvider {
    String getAlgorithm();
    String encrypt(String text) throws EncryptionException;
    String decrypt(String text) throws EncryptionException;
}
