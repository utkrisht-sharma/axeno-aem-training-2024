package com.assignment.core.crypto;

public interface AsymmetricCipherProvider {
    String getAlgorithm();
    String encrypt(String plaintext);
    String decrypt(String ciphertext);
}