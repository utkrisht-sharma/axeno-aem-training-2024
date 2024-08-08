package com.assignment.core.services.impl;

import com.assignment.core.services.config.CryptoConfiguration;
import com.assignment.core.services.CryptoService;
import com.assignment.core.services.exceptions.EncryptException;
import com.assignment.core.services.exceptions.DecryptException;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Component(service = CryptoService.class, immediate = true)
@Designate(ocd = CryptoConfiguration.class)
public class AsymmetricCryptoServiceImpl implements CryptoService {

    private static final Logger LOGGER = LogManager.getLogger(AsymmetricCryptoServiceImpl.class);

    private PublicKey encryptionKey;
    private PrivateKey decryptionKey;
    private String algorithm;

    @Activate
    @Modified
    protected void activate(CryptoConfiguration config) {
        try {
            algorithm = config.keyAlgorithm();

            KeyFactory keyFactory = KeyFactory.getInstance(algorithm);

            X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(config.publicKey()));
            encryptionKey = keyFactory.generatePublic(publicKeySpec);

            PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(config.privateKey()));
            decryptionKey = keyFactory.generatePrivate(privateKeySpec);

            LOGGER.info("Asymmetric crypto service initialized with {} algorithm", algorithm);
        } catch (Exception e) {
            LOGGER.error("Failed to initialize crypto service", e);
        }
    }

    @Override
    public String encryptMessage(String plaintext) throws EncryptException {
        try {
            Cipher cipher = Cipher.getInstance(algorithm + "/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, encryptionKey);
            byte[] encryptedBytes = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new EncryptException("Failed to encrypt message", e);
        }
    }

    @Override
    public String decryptMessage(String ciphertext) throws DecryptException {
        try {
            byte[] encryptedBytes = Base64.getDecoder().decode(ciphertext);
            Cipher cipher = Cipher.getInstance(algorithm + "/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, decryptionKey);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new DecryptException("Failed to decrypt message", e);
        }
    }
}
