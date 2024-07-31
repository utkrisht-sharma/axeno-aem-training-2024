package com.assignment.core.cipherservices;
public interface AsymmetricCipherProvider {
    String getAlgorithm();
    String encrypt(String text);
    String decrypt(String text);
}
