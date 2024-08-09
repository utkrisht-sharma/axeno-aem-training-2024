package com.assignment.core.services.impl;

import com.assignment.core.exceptions.EncryptionException;
import com.assignment.core.exceptions.ProviderNotFoundException;
import com.assignment.core.services.CipherService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(immediate = true)
public class CipherServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(CipherServiceTest.class);

    @Reference
    private CipherService cipherService;

    @Activate
    protected void activate() {
        runTest();
    }

    public void runTest() {
        String algorithm = "AES"; // or "AES" depending on the providers you have
        String plainText = "Hello, World!";

        logger.info("Starting encryption test...");
        try {
            String encryptedText = cipherService.encrypt(plainText, algorithm);
            logger.info("Encryption successful. Encrypted text: {}", encryptedText);

            String decryptedText = cipherService.decrypt(encryptedText, algorithm);
            logger.info("Decryption successful. Decrypted text: {}", decryptedText);

            if (plainText.equals(decryptedText)) {
                logger.info("Test passed: The decrypted text matches the original plain text.");
            } else {
                logger.error("Test failed: The decrypted text does not match the original plain text.");
            }
        } catch (EncryptionException | ProviderNotFoundException e) {
            logger.error("Encryption/Decryption test failed", e);
        }
    }
}

