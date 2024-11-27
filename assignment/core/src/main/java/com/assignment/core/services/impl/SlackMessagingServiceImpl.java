package com.assignment.core.services.impl;

import com.assignment.core.services.SlackMessagingService;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.Objects;

import static com.assignment.core.constants.OAuthConstants.*;

/**
 * Implementation of the SlackMessagingService
 * This service retrieves metadata from the specified asset, formats the metadata,
 * and posts it to a Slack channel.
 */
@Component(service = SlackMessagingService.class, immediate = true)
public class SlackMessagingServiceImpl implements SlackMessagingService {

    private static final String METADATA_PATH = "jcr:content/metadata";
    private static final String APPLICATION_JSON = "application/json";
    private static final String SLACK_MESSAGE_PAYLOAD_FORMAT = "{\"channel\": \"%s\", \"text\": \"%s\"}";
    private static final String METADATA_HEADER = "Asset Metadata:\n";
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss z";
    private static final Logger log = LoggerFactory.getLogger(SlackMessagingServiceImpl.class);

    @Reference
    private SlackMessagingConfigImpl slackMessagingConfig;

    /**
     * Sends the formatted metadata of an asset to a Slack channel.
     * @param payloadPath the path of the asset in the JCR
     * @param resourceResolver the ResourceResolver to access the asset
     * @param message a map containing the Slack channel and token for posting the message
     */
    @Override
    public void sendAssetMetadataToSlack(String payloadPath, ResourceResolver resourceResolver, Map<String, String> message) {
        Resource assetResource = resourceResolver.getResource(payloadPath);
        if (Objects.nonNull(assetResource)) {
            Resource metadataResource = assetResource.getChild(METADATA_PATH);
            if (Objects.nonNull(metadataResource)) {
                ValueMap metadataMap = metadataResource.getValueMap();
                String metadataMessage = formatMetadata(metadataMap);
                String channel = message.getOrDefault(SLACK_CHANNEL, slackMessagingConfig.getSlackChannel());
                String token = message.getOrDefault(SLACK_TOKEN, slackMessagingConfig.getSlackBotToken());
                String apiUrl = slackMessagingConfig.getSlackApiUrl();
                postToSlack(metadataMessage, channel, token, apiUrl);
            } else {
                log.warn("Metadata resource not found for asset at path: {}", payloadPath);
            }
        } else {
            log.warn("Asset resource not found for path: {}", payloadPath);
        }
    }

    /**
     * Posts the formatted metadata message to Slack using the provided details.
     *
     * @param message the message to post to Slack
     * @param channel the Slack channel to post the message to
     * @param token the OAuth token for Slack authentication
     * @param apiUrl the Slack API URL
     */
    private void postToSlack(String message, String channel, String token, String apiUrl) {
        log.info("Posting message to Slack channel: {}", channel);
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost postRequest = new HttpPost(apiUrl);
            postRequest.addHeader(AUTHORIZATION, BEARER + token);
            postRequest.addHeader(CONTENT_TYPE, APPLICATION_JSON);

            String payload = String.format(SLACK_MESSAGE_PAYLOAD_FORMAT, channel, message);
            postRequest.setEntity(new StringEntity(payload));

            CloseableHttpResponse response = httpClient.execute(postRequest);
            if (response.getStatusLine().getStatusCode() == 200) {
                log.info("Message posted successfully to Slack.");
            } else {
                log.error("Failed to post to Slack. Response: {}", response.getStatusLine());
            }
            response.close();
        } catch (IOException e) {
            log.error("Error posting message to Slack", e);
        }
    }

    /**
     * Formats the metadata retrieved from the asset into a human-readable string.
     * @param metadataMap the metadata of the asset
     * @return the formatted metadata string
     */
    private String formatMetadata(ValueMap metadataMap) {
        StringBuilder metadataInfo = new StringBuilder(METADATA_HEADER);
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        for (Map.Entry<String, Object> entry : metadataMap.entrySet()) {
            metadataInfo.append(entry.getKey()).append(": ");
            Object value = entry.getValue();
            if (value instanceof Calendar) {
                metadataInfo.append(dateFormat.format(((Calendar) value).getTime()));
            } else if (value instanceof Object[]) {
                metadataInfo.append(String.join(", ", (String[]) value));
            } else {
                metadataInfo.append(value.toString());
            }
            metadataInfo.append("\n");
        }
        return metadataInfo.toString();
    }
}
