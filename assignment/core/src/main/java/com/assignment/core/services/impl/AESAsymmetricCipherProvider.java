package com.assignment.core.services.impl;

import com.assignment.core.config.AESConfig;
import com.assignment.core.exceptions.EncryptionException;
import com.assignment.core.services.AsymmetricCipherProvider;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;


import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = AsymmetricCipherProvider.class, immediate = true)
@Designate(ocd = AESConfig.class)
public class AESAsymmetricCipherProvider implements AsymmetricCipherProvider {

    private static final Logger logger = LoggerFactory.getLogger(AESAsymmetricCipherProvider.class);

    private SecretKey secretKey;


    @Activate
    @Modified
    protected void activate(AESConfig aesConfig) {
        try {
            this.secretKey = createSecretKey(aesConfig.secretKey());

         } catch (NoSuchAlgorithmException e) {
            logger.error("Failed to initialize AES key", e);

        }
    }

    @Override
    public String getAlgorithm() {
        return "AES";
    }

    @Override
    public String encrypt(String text) throws EncryptionException {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedBytes = cipher.doFinal(text.getBytes());
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new EncryptionException("Encryption failed: Algorithm or padding scheme not found", e);
        } catch (InvalidKeyException e) {
            throw new EncryptionException("Encryption failed: Invalid key", e);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            throw new EncryptionException("Encryption failed: Illegal block size or bad padding", e);
        }
    }

    @Override
    public String decrypt(String text) throws EncryptionException {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decodedBytes = Base64.getDecoder().decode(text);
            byte[] decryptedBytes = cipher.doFinal(decodedBytes);
            return new String(decryptedBytes);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new EncryptionException("Decryption failed: Algorithm or padding scheme not found", e);
        } catch (InvalidKeyException e) {
            throw new EncryptionException("Decryption failed: Invalid key", e);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            throw new EncryptionException("Decryption failed: Illegal block size or bad padding", e);
        }
    }

    private SecretKey createSecretKey(String base64SecretKey) throws NoSuchAlgorithmException {
        byte[] keyBytes = Base64.getDecoder().decode(base64SecretKey);
        return new SecretKeySpec(keyBytes, "AES");
    }

}
