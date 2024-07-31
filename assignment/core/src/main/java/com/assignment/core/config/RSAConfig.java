package com.assignment.core.config;


import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name= "RSA configuration", description = "")
public @interface RSAConfig {
    @AttributeDefinition(
            name = "Public Key" ,
            type= AttributeType.STRING
    )
    String publicKey() default "";

    @AttributeDefinition(
            name = "Private Key" ,
            type= AttributeType.STRING
    )
    String privateKey() default "";
}
