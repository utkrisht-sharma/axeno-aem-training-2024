package com.assignment.core.config;


import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "AES Configuration")
public @interface AesConfiguration {
    @AttributeDefinition(
            name = "AES Key",
            type = AttributeType.STRING
    )
    String aesKey();

}
