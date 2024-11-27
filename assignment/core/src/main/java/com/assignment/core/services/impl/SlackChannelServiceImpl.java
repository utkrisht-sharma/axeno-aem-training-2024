package com.assignment.core.services.impl;

import com.assignment.core.services.SlackChannelService;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static com.assignment.core.constants.OAuthConstants.*;

/**
 * Implementation of the SlackChannelService interface.
 */
@Component(service = SlackChannelService.class, immediate = true)
public class SlackChannelServiceImpl implements SlackChannelService {

    @Reference
    private SlackAuthenticationConfigImpl slackAuthenticationConfig;

    private static final Logger LOGGER = LoggerFactory.getLogger(SlackChannelServiceImpl.class);

    /**
     * Fetches the list of channels from Slack using the provided access token.
     * @param accessToken the OAuth access token used for authorization in Slack API requests
     * @return a list of Slack channel names
     */
    @Override
    public List<String> getChannels(String accessToken) {
        List<String> channels = new ArrayList<>();
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(slackAuthenticationConfig.getChannelListUrl());
            httpGet.setHeader(AUTHORIZATION, BEARER + accessToken);
            httpGet.setHeader(CONTENT_TYPE, APPLICATION_URLENCODED);
            var response = httpClient.execute(httpGet);
            String responseString = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode == 200) {
                JsonObject jsonResponse = JsonParser.parseString(responseString).getAsJsonObject();
                if (jsonResponse.has(JSON_Response) && jsonResponse.get(JSON_Response).getAsBoolean()) {
                    JsonArray channelsArray = jsonResponse.getAsJsonArray(Channel);
                    for (int i = 0; i < channelsArray.size(); i++) {
                        JsonObject channel = channelsArray.get(i).getAsJsonObject();
                        String channelName = channel.get(Channel_Name).getAsString();
                        channels.add(channelName);
                    }
                } else {
                    LOGGER.error("Error fetching channels: {}", jsonResponse.get(ERROR_FIELD).getAsString());
                }
            } else {
                LOGGER.error("Failed to fetch channels. Status code: {}, Response: {}", statusCode, responseString);
            }
        } catch (IOException e) {
            LOGGER.error("Error during channels request: {}", e.getMessage());
        }

        return channels;
    }
}
