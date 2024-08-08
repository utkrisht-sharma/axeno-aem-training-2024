package com.assignment.core.services.impl;

import com.assignment.core.services.*;
import com.assignment.core.services.exceptions.DecryptionException;
import com.assignment.core.services.exceptions.EncryptionException;
import com.assignment.core.services.exceptions.ProviderNotFoundException;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



import java.util.ArrayList;
import java.util.List;

@Component(service = CipherService.class,
        immediate = true)
public class CipherServiceImpl implements CipherService {

    private static final Logger logger = LoggerFactory.getLogger(CipherServiceImpl.class);

    @Reference(
            service = AsymmetricCipherProvider.class,
            cardinality = ReferenceCardinality.MULTIPLE,
            policy = ReferencePolicy.DYNAMIC
    )
    private volatile  List<AsymmetricCipherProvider> cipherProviders = new ArrayList<>();

    protected void bindCipherProvider(AsymmetricCipherProvider provider) {
        logger.info("Binding CipherProvider for algorithm: {}", provider.getAlgorithm().toUpperCase());
        synchronized (cipherProviders) {
            cipherProviders.add(provider);
        }
    }

    protected void unbindCipherProvider(AsymmetricCipherProvider provider) {
        logger.info("Unbinding CipherProvider for algorithm: {}", provider.getAlgorithm().toUpperCase());
        synchronized (cipherProviders) {
            cipherProviders.remove(provider);
        }
    }

    @Override
    public String encrypt(String text, String algorithmType) throws EncryptionException {
        logger.info("Encrypting text with algorithm: {}", algorithmType);
        try {
            AsymmetricCipherProvider provider = getProvider(algorithmType);
            String encryptedText = provider.encrypt(text);
            logger.info("Encryption successful for algorithm: {}", algorithmType);
            return encryptedText;
        } catch (Exception e) {
            logger.error("Encryption failed for algorithm: {}", algorithmType, e);
            throw new EncryptionException("Encryption failed for algorithm: " + algorithmType, e);
        }
    }

    @Override
    public String decrypt(String text, String algorithmType) throws DecryptionException {
        logger.info("Decrypting text with algorithm: {}", algorithmType);
        try {
            AsymmetricCipherProvider provider = getProvider(algorithmType);
            String decryptedText = provider.decrypt(text);
            logger.info("Decryption successful for algorithm: {}", algorithmType);
            return decryptedText;
        } catch (Exception e) {
            logger.error("Decryption failed for algorithm: {}", algorithmType, e);
            throw new DecryptionException("Decryption failed for algorithm: " + algorithmType, e);
        }
    }

    private AsymmetricCipherProvider getProvider(String algorithmType) throws ProviderNotFoundException {
        synchronized (cipherProviders) {
            for (AsymmetricCipherProvider provider : cipherProviders) {
                if (provider.getAlgorithm().equalsIgnoreCase(algorithmType)) {
                    return provider;
                }
            }
            logger.error("No provider found for algorithm: {}", algorithmType);
            throw new ProviderNotFoundException("No provider found for algorithm: " + algorithmType);
        }
    }
}
