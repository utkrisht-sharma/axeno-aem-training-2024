package com.assignment.core.config;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "RSA Encryption Configuration", description = "Configuration for RSA encryption and decryption keys")
public @interface RSAConfig {

    @AttributeDefinition(
            name = "Public Key",
            description = "Base64 encoded public key",
            type = AttributeType.STRING
    )
    String publicKey() default "";

    @AttributeDefinition(
            name = "Private Key",
            description = "Base64 encoded private key",
            type = AttributeType.STRING
    )
    String privateKey() default "";
}




