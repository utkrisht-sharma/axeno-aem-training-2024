package com.assignment.core.cipherservices.impl;




import com.assignment.core.cipherservices.AsymmetricCipherProvider;
import com.assignment.core.cipherservices.CipherService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicyOption;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component(service = CipherService.class, immediate = true)
public class CipherServiceImpl implements CipherService {

    private final Map<String, AsymmetricCipherProvider> providers = new ConcurrentHashMap<>();

    @Reference(policyOption = ReferencePolicyOption.GREEDY)
    public void bindAsymmetricCipherProvider(AsymmetricCipherProvider provider) {
        providers.put(provider.getAlgorithm(), provider);
    }

    public void unbindAsymmetricCipherProvider(AsymmetricCipherProvider provider) {
        providers.remove(provider.getAlgorithm());
    }

    @Override
    public String encrypt(String text, String algorithm) {
        AsymmetricCipherProvider provider = providers.get(algorithm);
        if (provider != null) {
            return provider.encrypt(text);
        } else {
            throw new IllegalArgumentException("No provider found for algorithm: " + algorithm);
        }
    }

    @Override
    public String decrypt(String text, String algorithm) {
        AsymmetricCipherProvider provider = providers.get(algorithm);
        if (provider != null) {
            return provider.decrypt(text);
        } else {
            throw new IllegalArgumentException("No provider found for algorithm: " + algorithm);
        }
    }
}

