package com.assignment.core.services.serviceImpl;

import com.assignment.core.services.SpotifyIntegrationConfig;
import com.assignment.core.services.SpotifyIntegrationService;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Base64;
import java.nio.charset.StandardCharsets;

/**
 * Implementation of SpotifyIntegrationService for managing Spotify API interactions.
 * Handles authentication, token management, and song retrieval.
 */
@Component(service = SpotifyIntegrationService.class, immediate = true)
@Designate(ocd = SpotifyIntegrationConfig.class)
public class SpotifyIntegrationServiceImpl implements SpotifyIntegrationService {

    private static final Logger log = LoggerFactory.getLogger(SpotifyIntegrationServiceImpl.class);

    private String clientId;
    private String clientSecret;
    private String redirectUri;
    private String spotifyApiBaseUrl;
    private String spotifyLikedSongsUrl;

    /**
     * Initializes and configures the Spotify Integration Service.
     *
     * @param config Configuration settings for Spotify integration
     */
    @Activate
    @Modified
    public void activate(SpotifyIntegrationConfig config) {
        this.clientId = config.clientId();
        this.clientSecret = config.clientSecret();
        this.redirectUri = config.redirectUri();
        this.spotifyApiBaseUrl = config.spotifyApiBaseUrl();
        this.spotifyLikedSongsUrl = config.spotifyLikedSongsUrl();
        log.info("Spotify Integration Service activated with clientId: {}", this.clientId);
    }

    /**
     * Generates the Spotify authorization URL for OAuth2 authentication.
     *
     * @return Fully formed Spotify authorization URL
     */
    @Override
    public String getAuthorizationUrl() {
        String url = String.format("%s/authorize?client_id=%s&response_type=code&redirect_uri=%s&scope=user-library-read",
                this.spotifyApiBaseUrl, this.clientId, this.redirectUri);
        log.info("Generated Spotify authorization URL: {}", url);
        return url;
    }

    /**
     * Retrieves access token using the provided authorization code.
     *
     * @param authorizationCode Authorization code received from Spotify
     * @return JSON string containing access token
     */
    @Override
    public String getAccessToken(String authorizationCode) {
        try {
            List<BasicNameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("grant_type", "authorization_code"));
            params.add(new BasicNameValuePair("code", authorizationCode));
            params.add(new BasicNameValuePair("redirect_uri", this.redirectUri));

            String response = makeTokenRequest(params);
            log.info("Successfully retrieved access token for authorization code: {}", authorizationCode);
            return response;
        } catch (IOException e) {
            log.error("Error retrieving access token for authorization code: {}", authorizationCode, e);
            return "{}";  // Return empty JSON
        }
    }

    /**
     * Refreshes the access token using a refresh token.
     *
     * @param refreshToken Refresh token obtained from previous authentication
     * @return JSON string with new access token
     */
    @Override
    public String refreshAccessToken(String refreshToken) {
        try {
            List<BasicNameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("grant_type", "refresh_token"));
            params.add(new BasicNameValuePair("refresh_token", refreshToken));

            String response = makeTokenRequest(params);
            log.info("Successfully refreshed access token.");
            return response;
        } catch (IOException e) {
            log.error("Error refreshing access token using refresh token: {}", refreshToken, e);
            return "{}";  // Return empty JSON
        }
    }

    /**
     * Makes the token request to Spotify API.
     *
     * @param params Request parameters for token generation
     * @return JSON string containing token information
     */
    private String makeTokenRequest(List<BasicNameValuePair> params) throws IOException {
        String authHeader = Base64.getEncoder().encodeToString(
                (this.clientId + ":" + this.clientSecret).getBytes(StandardCharsets.UTF_8)
        );

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(this.spotifyApiBaseUrl + "/api/token");
            post.setHeader("Authorization", "Basic " + authHeader);
            post.setHeader("Content-Type", "application/x-www-form-urlencoded");
            post.setEntity(new UrlEncodedFormEntity(params));

            String response = EntityUtils.toString(client.execute(post).getEntity());
            log.info("Token request successfully completed.");
            return response;
        }
        catch (IOException e) {
            log.error("Error making token request: {}", e);
            throw e;
        }
    }

    /**
     * Retrieves user's liked songs from Spotify.
     *
     * @param accessToken Valid access token for Spotify API
     * @return JSON string of liked songs
     */
    @Override
    public String getLikedSongs(String accessToken) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet get = new HttpGet(this.spotifyLikedSongsUrl + "?limit=30");
            get.setHeader("Authorization", "Bearer " + accessToken);

            String jsonResponse = EntityUtils.toString(client.execute(get).getEntity());
            log.info("Successfully retrieved liked songs from Spotify.");
            return jsonResponse;
        } catch (IOException e) {
            log.error("Error retrieving liked songs for accessToken: {}", accessToken, e);
            return "{}";  // Return empty JSON
        }
    }
}
