package com.assignment.core.services;



public interface CipherService {
    String encrypt(String text, String algorithmType) throws CipherException;
    String decrypt(String text, String algorithmType) throws CipherException;
}
