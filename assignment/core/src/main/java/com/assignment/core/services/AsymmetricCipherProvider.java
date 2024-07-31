package com.assignment.core.services;

public interface AsymmetricCipherProvider {
    String getAlgorithm();

    String encrypt(String plainText) throws Exception;

    String decrypt(String cipherText) throws Exception;
}
