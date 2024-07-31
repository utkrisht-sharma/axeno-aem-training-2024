package com.assignment.core.services;

public interface AsymmetricCipherProvider {
    String getAlgorithm();
    String encrypt(String text) throws Exception;
    String decrypt(String text) throws Exception;
}