package com.assignment.core.services;

import org.apache.sling.api.resource.ResourceResolver;

import java.util.Map;

public interface SlackMessagingService {
    void sendAssetMetadataToSlack(String payloadPath, ResourceResolver resourceResolver, Map<String, String> message);
}
