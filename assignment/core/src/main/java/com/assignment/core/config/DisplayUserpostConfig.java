package com.assignment.core.config;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Display User post Service Configuration Url")
public @interface DisplayUserpostConfig {
    @AttributeDefinition(name = "Post API URL", description = "URL for the Post API")
    String apiUrl();
}
