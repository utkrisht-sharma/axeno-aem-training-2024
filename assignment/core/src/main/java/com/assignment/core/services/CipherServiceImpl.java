package com.assignment.core.services;


import com.assignment.core.crypto.AsymmetricCipherProvider;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component(service = CipherService.class)
public class CipherServiceImpl implements CipherService {

    @Reference(
            service = AsymmetricCipherProvider.class,
            cardinality = ReferenceCardinality.MULTIPLE,
            policy = ReferencePolicy.DYNAMIC
    )
    private volatile Map<String, AsymmetricCipherProvider> cipherProviders = new ConcurrentHashMap<>();

    protected void bindCipherProvider(AsymmetricCipherProvider provider) {
        cipherProviders.put(provider.getAlgorithm().toUpperCase(), provider);
    }

    protected void unbindCipherProvider(AsymmetricCipherProvider provider) {
        cipherProviders.remove(provider.getAlgorithm().toUpperCase());
    }

    @Override
    public String encrypt(String text, String algorithmType) throws CipherException {
        try {
            AsymmetricCipherProvider provider = getProvider(algorithmType);
            return provider.encrypt(text);
        } catch (Exception e) {
            throw new CipherException("Encryption failed for algorithm: " + algorithmType, e);
        }
    }

    @Override
    public String decrypt(String text, String algorithmType) throws CipherException {
        try {
            AsymmetricCipherProvider provider = getProvider(algorithmType);
            return provider.decrypt(text);
        } catch (Exception e) {
            throw new CipherException("Decryption failed for algorithm: " + algorithmType, e);
        }
    }

    private AsymmetricCipherProvider getProvider(String algorithmType) throws CipherException {
        AsymmetricCipherProvider provider = cipherProviders.get(algorithmType.toUpperCase());
        if (provider == null) {
            throw new CipherException("No provider found for algorithm: " + algorithmType);
        }
        return provider;
    }
}

