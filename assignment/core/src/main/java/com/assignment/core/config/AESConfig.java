package com.assignment.core.config;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "AES Encryption Configuration", description = "Configuration for AES secret key")
public @interface AESConfig {

    @AttributeDefinition(
            name = "Secret Key",
            description = "Base64 encoded secret key",
            type = AttributeType.STRING
    )
    String secretKey() default "";


}
