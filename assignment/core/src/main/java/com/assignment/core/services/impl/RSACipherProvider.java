package com.assignment.core.services.impl;

import com.assignment.core.services.AsymmetricCipherProvider;
import com.assignment.core.services.exceptions.DecryptionException;
import com.assignment.core.services.exceptions.EncryptionException;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Component(
        service = AsymmetricCipherProvider.class,
        immediate = true
//        property = {
//                "algorithm=RSA"
//        }
)
public class RSACipherProvider implements AsymmetricCipherProvider {
    private static final String ALGORITHM = "RSA";
    private static final Logger logger = LoggerFactory.getLogger(RSACipherProvider.class);
    private KeyPair keyPair;

    public RSACipherProvider() throws NoSuchAlgorithmException {
        try {
            logger.info("Initializing RSA KeyPairGenerator");
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM);
            keyGen.initialize(2048);
            this.keyPair = keyGen.generateKeyPair();
            logger.info("RSA key pair successfully initialized");
        } catch (NoSuchAlgorithmException e) {
            logger.error("Failed to initialize RSA key pair", e);
            throw e; // Re-throw the exception to be handled by the caller
        }
    }

    @Override
    public String getAlgorithm() {
        return ALGORITHM;
    }

    @Override
    public String encrypt(String plaintext) throws EncryptionException {
        try {
            logger.info("Encrypting text with RSA algorithm");
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, keyPair.getPublic());
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
            logger.info("Decrypting text with RSA algorithm");
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
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

