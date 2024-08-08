package com.assignment.core.services.config;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(
        name = "Crypto Configuration",
        description = "Configuration for asymmetric encryption and decryption keys"
)
public @interface CryptoConfiguration {

    @AttributeDefinition(name = "Public Key", description = "Base64 encoded public key")
    String publicKey();

    @AttributeDefinition(name = "Private Key", description = "Base64 encoded private key")
    String privateKey();

    @AttributeDefinition(name = "Key Algorithm", description = "Algorithm used for key generation (e.g., RSA, EC)")
    String keyAlgorithm() default "RSA";
}