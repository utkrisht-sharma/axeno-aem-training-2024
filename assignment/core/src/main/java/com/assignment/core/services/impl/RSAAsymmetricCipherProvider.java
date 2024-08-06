package com.assignment.core.services.impl;

import com.assignment.core.exceptions.EncryptionException;
import com.assignment.core.services.AsymmetricCipherProvider;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import javax.crypto.Cipher;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = AsymmetricCipherProvider.class)
public class RSAAsymmetricCipherProvider implements AsymmetricCipherProvider {

    private static final Logger logger = LoggerFactory.getLogger(RSAAsymmetricCipherProvider.class);

    private PublicKey publicKey;
    private PrivateKey privateKey;

    @Activate
    @Modified
    protected void activate() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048); // Key size in bits
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            this.publicKey = keyPair.getPublic();
            this.privateKey = keyPair.getPrivate();
        } catch (NoSuchAlgorithmException e) {
            logger.error("Failed to initialize RSA key pair", e);

        }
    }

    @Override
    public String getAlgorithm() {
        return "RSA";
    }

    @Override
    public String encrypt(String text) throws EncryptionException {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] encryptedBytes = cipher.doFinal(text.getBytes());
            return Base64.getEncoder().encodeToString(encryptedBytes);

        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new EncryptionException("Encryption failed: Algorithm or padding scheme not found", e);
        } catch (InvalidKeyException e) {
            throw new EncryptionException("Encryption failed: Invalid key", e);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            throw new EncryptionException("Encryption failed: Illegal block size or bad padding", e);
        } catch (Exception e) {
            throw new EncryptionException("Encryption failed", e);
        }
    }

    @Override
    public String decrypt(String text) throws EncryptionException {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] decodedBytes = Base64.getDecoder().decode(text);
            byte[] decryptedBytes = cipher.doFinal(decodedBytes);
            return new String(decryptedBytes);

        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new EncryptionException("Decryption failed: Algorithm or padding scheme not found", e);
        } catch (InvalidKeyException e) {
            throw new EncryptionException("Decryption failed: Invalid key", e);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            throw new EncryptionException("Decryption failed: Illegal block size or bad padding", e);
        } catch (Exception e) {
            throw new EncryptionException("Decryption failed", e);
        }
    }


}
