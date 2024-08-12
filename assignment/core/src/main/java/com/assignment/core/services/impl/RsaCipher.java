package com.assignment.core.services.impl;


import com.assignment.core.config.RsaConfiguration;
import com.assignment.core.services.AsymmetricCipherProvider;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;

import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Component(service = AsymmetricCipherProvider.class, property = {"algorithm=RSA"},configurationPolicy = ConfigurationPolicy.REQUIRE,immediate = true)
@Designate(ocd = RsaConfiguration.class)
public class RsaCipher implements AsymmetricCipherProvider {
    private static final Logger rsaLogger = Logger.getLogger(RsaCipher.class.getName());
    private PrivateKey privateKey;
    private PublicKey publicKey;

    @Activate
    @Modified
    public void activate(RsaConfiguration config) {
        try {
            initKeys(config.privateKey(), config.publicKey());
        } catch (NoSuchAlgorithmException alorithmException) {
            rsaLogger.info("NoSuchAlgorithmException In RsaCipher:Occured While Calling initKeys() method");
        } catch (InvalidKeySpecException keyException) {
            rsaLogger.info("InvalidKeySpecException In RsaCipher:Occured While Calling initKeys() method");
        }

    }

    void initKeys(String privateKeyString, String publicKeyString) throws NoSuchAlgorithmException, InvalidKeySpecException {
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
    public String encrypt(String text) throws NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        String encryptedValue;
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedBytes = cipher.doFinal(text.getBytes());
        encryptedValue = Base64.getEncoder().encodeToString(encryptedBytes);
        return encryptedValue;
    }

    @Override
    public String decrypt(String text) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        String decryptedValue;
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(text));
        decryptedValue = new String(decryptedBytes);
        return decryptedValue;
    }
}
