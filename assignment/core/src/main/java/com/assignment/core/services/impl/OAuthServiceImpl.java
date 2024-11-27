package com.assignment.core.services.impl;

import com.assignment.core.services.OAuthService;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;

import static com.assignment.core.constants.OAuthConstants.*;


@Component(service = OAuthService.class, immediate = true)
public class OAuthServiceImpl implements OAuthService {

    private static final Logger logger = LoggerFactory.getLogger(OAuthServiceImpl.class);
    @Reference
    private SlackAuthenticationConfigImpl slackAuthenticationConfig;

    @Override
    public String getAccessToken(String code) {
        String accessToken = StringUtils.EMPTY;
        String tokenUrl = slackAuthenticationConfig.getTokenUrl();
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(tokenUrl);
            String body = String.format(ACCESS_TOKEN_BODY, slackAuthenticationConfig.getClientID(), slackAuthenticationConfig.getClientSecret(), code, slackAuthenticationConfig.getRedirectUri());
            httpPost.setEntity(new StringEntity(body));
            httpPost.setHeader(CONTENT_TYPE, APPLICATION_URLENCODED);

            var tokenResponse = httpClient.execute(httpPost);
            String responseString = EntityUtils.toString(tokenResponse.getEntity(), StandardCharsets.UTF_8);
            int statusCode = tokenResponse.getStatusLine().getStatusCode();

            if (statusCode == 200) {
                JsonObject jsonObject = JsonParser.parseString(responseString).getAsJsonObject();
                if (jsonObject.has(ACCESS_TOKEN_FIELD)) {
                    accessToken = jsonObject.get(ACCESS_TOKEN_FIELD).getAsString();
                } else if (jsonObject.has(ERROR_FIELD)) {
                    logger.error("Error fetching access token: {}", jsonObject.get(ERROR_FIELD).getAsString());
                }
            } else {
                logger.error("Failed to fetch access token. Status code: {}, Response: {}", statusCode, responseString);
            }
        } catch (IOException e) {
            logger.error("Error during access token request: {}", e.getMessage());
        }
        return accessToken;
    }
}