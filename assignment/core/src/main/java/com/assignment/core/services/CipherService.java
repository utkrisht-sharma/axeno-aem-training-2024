package com.assignment.core.services;


import com.assignment.core.services.exceptions.CipherException;

public interface CipherService {
    String encrypt(String text, String algorithmType) throws CipherException;
    String decrypt(String text, String algorithmType) throws CipherException;
}
