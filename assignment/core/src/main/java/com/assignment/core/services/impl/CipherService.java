package com.assignment.core.services.impl;

import com.assignment.core.services.AsymmetricCipherProvider;
import opennlp.tools.util.StringUtil;
import org.apache.jackrabbit.oak.commons.StringUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.Objects;
import java.util.logging.Logger;

@Component
public class CipherService {
    private static final Logger cipherLogger = Logger.getLogger(CipherService.class.getName());
    @Reference(target = "(algorithm=RSA)")
    private AsymmetricCipherProvider rsaProvider;

    @Reference(target = "(algorithm=AES)")
    private AsymmetricCipherProvider aesProvider;

    public String encrypt(String text, String algorithm) throws Exception {
        AsymmetricCipherProvider provider = getProvider(algorithm);
        String encryptedValue;
        if (!Objects.nonNull(provider)) {
            return "Provided Algorithm Is Not Supported. ";
        }
        encryptedValue = provider.encrypt(text);
        cipherLogger.info("Encrypted Value Using " + algorithm + " : " + encryptedValue);
        return encryptedValue;
    }

    public String decrypt(String text, String algorithm) throws Exception {
        AsymmetricCipherProvider provider = getProvider(algorithm);
        String decryptedValue;
        decryptedValue = provider.decrypt(text);
        cipherLogger.info("Decrypted Value Using " + algorithm + " : " + decryptedValue);
        return decryptedValue;
    }

    private AsymmetricCipherProvider getProvider(String algorithm) {
        if ("RSA".equalsIgnoreCase(algorithm)) {
            return rsaProvider;
        } else if ("AES".equalsIgnoreCase(algorithm)) {
            return aesProvider;
        } else {
            return null;
        }
    }
}
