package com.assignment.core.services.impl;

import com.assignment.core.config.AesConfiguration;
import com.assignment.core.services.AsymmetricCipherProvider;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Component(service = AsymmetricCipherProvider.class, property = {"algorithm=AES"}, configurationPolicy = ConfigurationPolicy.REQUIRE, immediate = true)
@Designate(ocd = AesConfiguration.class)
public class AesCipher implements AsymmetricCipherProvider {
    private SecretKey secretKey;

    @Activate
    @Modified
    void activate(AesConfiguration config) {
        initKey(config.aesKey());
    }

    public void initKey(String keyString) {
        byte[] decodedKey = Base64.getDecoder().decode(keyString);
        this.secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
    }

    @Override
    public String getAlgorithm() {
        return "AES";
    }

    @Override
    public String encrypt(String text) throws NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException {
        String encryptedValue;
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal(text.getBytes());
        encryptedValue = Base64.getEncoder().encodeToString(encryptedBytes);
        return encryptedValue;
    }

    @Override
    public String decrypt(String text) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        String decryptedValue;
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(text));
        decryptedValue = new String(decryptedBytes);
        return decryptedValue;
    }
}