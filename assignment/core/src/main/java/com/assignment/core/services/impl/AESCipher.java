package com.assignment.core.services.impl;
import com.assignment.core.config.RSA_AES_Config;
import com.assignment.core.services.AsymmetricCipherProvider;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Component(service = AsymmetricCipherProvider.class,
        configurationPolicy = ConfigurationPolicy.REQUIRE,
        property = { "algorithm=AES" }
)
@Designate(ocd = RSA_AES_Config.class)
public class AESCipher implements AsymmetricCipherProvider {

    private SecretKey secretKey;

    @Activate
    @Modified
    public void activate(RSA_AES_Config config) {
        try {
            initKey(config.aesKey());
        } catch (Exception e) {
            e.printStackTrace();
        }
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
    public String encrypt(String text) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal(text.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    @Override
    public String decrypt(String text) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(text));
        return new String(decryptedBytes);
    }
}