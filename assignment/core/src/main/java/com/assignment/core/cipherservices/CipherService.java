package com.assignment.core.cipherservices;
public interface CipherService {
    String encrypt(String text, String algorithm);
    String decrypt(String text, String algorithm);
}

