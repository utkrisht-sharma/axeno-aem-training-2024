package com.assignment.core.services.impl;

import com.assignment.core.exceptions.EncryptionException;
import com.assignment.core.services.AsymmetricCipherProvider;
import com.assignment.core.services.CipherService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.List;
import java.util.Optional;

@Component(service = CipherService.class)
public class CipherServiceImpl implements CipherService {

    @Reference
    private List<AsymmetricCipherProvider> providers;

    @Override
    public String encrypt(String plainText, String algorithm) throws EncryptionException {
        AsymmetricCipherProvider provider = findProvider(algorithm)
                .orElseThrow(() -> new EncryptionException("No provider found for: " + algorithm));
        return provider.encrypt(plainText);
    }

    @Override
    public String decrypt(String cipherText, String algorithm) throws EncryptionException {
        AsymmetricCipherProvider provider = findProvider(algorithm)
                .orElseThrow(() -> new EncryptionException("No provider found for: " + algorithm));
        return provider.decrypt(cipherText);
    }

    private Optional<AsymmetricCipherProvider> findProvider(String algorithm) {
        return providers.stream()
                .filter(provider -> provider.getAlgorithm().equalsIgnoreCase(algorithm))
                .findFirst();
    }
}

