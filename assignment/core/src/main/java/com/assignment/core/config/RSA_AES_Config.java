package com.assignment.core.config;


import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "RSA and AES Configuration")
public @interface RSA_AES_Config {
    @AttributeDefinition(
            name = "Public Key" ,
            type= AttributeType.STRING
    )
    String publicKey() default "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCYQYM3FwZuQNLK7xRbuBbA...";

    @AttributeDefinition(
            name = "Private Key" ,
            type= AttributeType.STRING
    )
    String privateKey() default "MIICdgIBADANBgkqhkiG9w0BAQEFA...";
    @AttributeDefinition(
            name = "AES Key",
            type = AttributeType.STRING
    )
    String aesKey() default "U29tZVByb3ZpZGVQYXNzd29yZA==";
}
