package com.assignment.core.services.impl;

import com.assignment.core.config.EncDecConfig;
import com.assignment.core.services.EncryptionDecryptionService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Component(service = EncryptionDecryptionService.class)
@Designate(ocd = EncDecConfig.class)
public class EncryptionDecryptionServiceImpl implements EncryptionDecryptionService {

    private PrivateKey privateKey;
    private PublicKey publicKey;

    @Activate
    public void activate(EncDecConfig rsaConfig) {
        try {
            X509EncodedKeySpec keySpecPublic = new X509EncodedKeySpec(decode(rsaConfig.publicKey()));
            PKCS8EncodedKeySpec keySpecPrivate = new PKCS8EncodedKeySpec(decode(rsaConfig.privateKey()));

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            publicKey = keyFactory.generatePublic(keySpecPublic);
            privateKey = keyFactory.generatePrivate(keySpecPrivate);
        } catch (Exception e) {
            e.printStackTrace(); // Handle the exception appropriately
        }
    }

    @Override
    public String encrypt(String message) throws Exception {
        byte[] messageToBytes = message.getBytes(StandardCharsets.UTF_8);
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedBytes = cipher.doFinal(messageToBytes);
        return encode(encryptedBytes);
    }

    @Override
    public String decrypt(String encryptedMessage) throws Exception {
        byte[] encryptedBytes = decode(encryptedMessage);
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedMessage = cipher.doFinal(encryptedBytes);
        return new String(decryptedMessage, StandardCharsets.UTF_8);
    }

    private static String encode(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }

    private static byte[] decode(String data) {
        return Base64.getDecoder().decode(data);
    }
}
