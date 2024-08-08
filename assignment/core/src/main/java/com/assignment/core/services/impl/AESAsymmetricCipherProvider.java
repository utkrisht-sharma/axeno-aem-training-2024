package com.assignment.core.services.impl;

import com.assignment.core.services.AsymmetricCipherProvider;
import org.osgi.service.component.annotations.Component;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.util.Base64;

@Component(service = AsymmetricCipherProvider.class, property = "algorithm=AES")
public class AESAsymmetricCipherProvider implements AsymmetricCipherProvider {

    private final SecretKey secretKey;

    public AESAsymmetricCipherProvider() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256);
        this.secretKey = keyGen.generateKey();
    }

    @Override
    public String getAlgorithm() {
        return "AES";
    }

    @Override
    public String encrypt(String plainText) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    @Override
    public String decrypt(String cipherText) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(cipherText));
        return new String(decryptedBytes);
    }
}
