package com.assignment.core.services.impl;


import com.assignment.core.config.RSA_AES_Config;
import com.assignment.core.services.AsymmetricCipherProvider;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;

import javax.crypto.Cipher;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Component(service = AsymmetricCipherProvider.class,
        property = { "algorithm=RSA" }
)
@Designate(ocd = RSA_AES_Config.class)
public class RSACipher implements AsymmetricCipherProvider {

    private PrivateKey privateKey;
    private PublicKey publicKey;
    @Activate
    @Modified
    public void activate(RSA_AES_Config config) {
        try {
            initKeys(config.privateKey(), config.publicKey());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initKeys(String privateKeyString, String publicKeyString) throws Exception {
        X509EncodedKeySpec keySpecPublic = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyString));
        PKCS8EncodedKeySpec keySpecPrivate = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyString));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        this.publicKey = keyFactory.generatePublic(keySpecPublic);
        this.privateKey = keyFactory.generatePrivate(keySpecPrivate);
    }

    @Override
    public String getAlgorithm() {
        return "RSA";
    }

    @Override
    public String encrypt(String text) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedBytes = cipher.doFinal(text.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    @Override
    public String decrypt(String text) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(text));
        return new String(decryptedBytes);
    }
}
