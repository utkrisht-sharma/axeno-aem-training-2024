package com.assignment.core.services.impl;

import com.assignment.core.services.AsymmetricCipherProvider;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import java.util.HashMap;
import java.util.Map;

@Component(service = CipherService.class)
public class CipherService {

    private final Map<String, com.assignment.core.services.AsymmetricCipherProvider> providers = new HashMap<>();

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    protected void bindAsymmetricCipherProvider(com.assignment.core.services.AsymmetricCipherProvider provider) {
        providers.put(provider.getAlgorithm(), provider);
    }

    protected void unbindAsymmetricCipherProvider(AsymmetricCipherProvider provider) {
        providers.remove(provider.getAlgorithm());
    }

    public String encrypt(String plainText, String algorithm) throws Exception {
        AsymmetricCipherProvider provider = providers.get(algorithm);
        if (provider != null) {
            return provider.encrypt(plainText);
        }
        throw new IllegalArgumentException("No provider found for algorithm: " + algorithm);
    }

    public String decrypt(String cipherText, String algorithm) throws Exception {
        AsymmetricCipherProvider provider = providers.get(algorithm);
        if (provider != null) {
            return provider.decrypt(cipherText);
        }
        throw new IllegalArgumentException("No provider found for algorithm: " + algorithm);
    }
}
