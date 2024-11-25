package com.assignment.core.services;

/**
 * Service interface for managing Spotify API integration.
 * Handles authentication, token management, and data retrieval from Spotify.
 */
public interface SpotifyIntegrationService {

    /**
     * Generates the Spotify authorization URL for OAuth2 authentication.
     *
     * @return Fully formed authorization URL to initiate Spotify login
     */
    String getAuthorizationUrl();

    /**
     * Retrieves access token using the provided authorization code.
     *
     * @param authorizationCode Authorization code received from Spotify
     * @return JSON string containing access token and related information
     */
    String getAccessToken(String authorizationCode);

    /**
     * Refreshes the access token using a refresh token.
     *
     * @param refreshToken Refresh token obtained from previous authentication
     * @return JSON string with new access token and related information
     */
    String refreshAccessToken(String refreshToken);

    /**
     * Retrieves a user's liked songs from Spotify.
     *
     * @param accessToken Valid access token for Spotify API
     * @return JSON string containing user's liked songs
     */
    String getLikedSongs(String accessToken);
}
