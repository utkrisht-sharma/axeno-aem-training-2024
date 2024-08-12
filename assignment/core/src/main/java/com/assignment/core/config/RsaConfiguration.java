package com.assignment.core.config;


import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "RSA Configuration")
public @interface RsaConfiguration {
    @AttributeDefinition(
            name = "Public Key",
            type = AttributeType.STRING
    )
    String publicKey();

    @AttributeDefinition(
            name = "Private Key",
            type = AttributeType.STRING
    )
    String privateKey();

}
