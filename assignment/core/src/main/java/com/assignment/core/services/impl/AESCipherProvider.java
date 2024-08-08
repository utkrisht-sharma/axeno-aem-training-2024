package com.assignment.core.services.impl;

import com.assignment.core.services.AsymmetricCipherProvider;
import com.assignment.core.services.exceptions.DecryptionException;
import com.assignment.core.services.exceptions.EncryptionException;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Component(
        service = AsymmetricCipherProvider.class,
        immediate = true
)
public class AESCipherProvider implements AsymmetricCipherProvider {
    private static final String ALGORITHM = "AES";
    private static final Logger logger = LoggerFactory.getLogger(AESCipherProvider.class);
    private SecretKey secretKey;

    public AESCipherProvider() throws NoSuchAlgorithmException {
        try {
            logger.info("Initializing AES KeyGenerator");
            KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
            keyGen.init(256);
            this.secretKey = keyGen.generateKey();
            logger.info("AES key successfully initialized");
        } catch (NoSuchAlgorithmException e) {
            logger.error("Failed to initialize AES key", e);
            throw new NoSuchAlgorithmException("Encryption failed", e);
        }
    }

    @Override
    public String getAlgorithm() {
        return ALGORITHM;
    }

    @Override
    public String encrypt(String plaintext) throws EncryptionException {
        try {
            logger.info("Encrypting text with AES algorithm");
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedBytes = cipher.doFinal(plaintext.getBytes());
            String encryptedText = Base64.getEncoder().encodeToString(encryptedBytes);
            logger.info("Encryption successful");
            return encryptedText;
        } catch (Exception e) {
            logger.error("Encryption failed", e);
            throw new EncryptionException("Encryption failed", e);
        }
    }

    @Override
    public String decrypt(String ciphertext) throws DecryptionException {
        try {
            logger.info("Decrypting text with AES algorithm");
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(ciphertext));
            String decryptedText = new String(decryptedBytes);
            logger.info("Decryption successful");
            return decryptedText;
        } catch (Exception e) {
            logger.error("Decryption failed", e);
            throw new DecryptionException("Decryption failed", e);
        }
    }
}

