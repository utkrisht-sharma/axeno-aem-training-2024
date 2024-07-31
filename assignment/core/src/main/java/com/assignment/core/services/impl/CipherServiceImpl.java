package com.assignment.core.services.impl;

import com.assignment.core.services.AsymmetricCipherProvider;
import com.assignment.core.services.CipherService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import java.util.List;


@Component(service = CipherService.class)
public class CipherServiceImpl implements CipherService {

    @Reference
    private List<AsymmetricCipherProvider> providers;


    public String encrypt(String plainText, String algorithm) throws Exception {
        AsymmetricCipherProvider provider = findProvider(algorithm);
        if (provider != null) {
            return provider.encrypt(plainText);
        }
        throw new IllegalArgumentException("No provider found for: " + algorithm);
    }

    public String decrypt(String cipherText, String algorithm) throws Exception {
        AsymmetricCipherProvider provider = findProvider(algorithm);
        if (provider != null) {
            return provider.decrypt(cipherText);
        }
        throw new IllegalArgumentException("No provider found for : " + algorithm);
    }

    private AsymmetricCipherProvider findProvider(String algorithm) {
        return   providers.stream()
                .filter(provider -> provider.getAlgorithm().equalsIgnoreCase(algorithm))
                .findFirst()
                .orElse(null);
    }
}

