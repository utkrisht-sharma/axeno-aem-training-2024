package com.assignment.core.services.impl;

import com.assignment.core.services.AsymmetricCipherProvider;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(service = CipherService.class)
public class CipherService {

    @Reference(target = "(algorithm=RSA)")
    private AsymmetricCipherProvider rsaProvider;

    @Reference(target = "(algorithm=AES)")
    private AsymmetricCipherProvider aesProvider;

    public String encrypt(String text, String algorithm) throws Exception {
        AsymmetricCipherProvider provider = getProvider(algorithm);
        return provider.encrypt(text);
    }

    public String decrypt(String text, String algorithm) throws Exception {
        AsymmetricCipherProvider provider = getProvider(algorithm);
        return provider.decrypt(text);
    }

    private AsymmetricCipherProvider getProvider(String algorithm) {
        if ("RSA".equalsIgnoreCase(algorithm)) {
            return rsaProvider;
        } else if ("AES".equalsIgnoreCase(algorithm)) {
            return aesProvider;
        } else {
            throw new IllegalArgumentException("Unsupported algorithm: " + algorithm);
        }
    }
}
