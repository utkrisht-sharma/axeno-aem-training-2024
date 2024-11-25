package com.assignment.core.services;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
/**
 * Configuration interface for Spotify Integration Service.
 * This interface defines configuration parameters for connecting to and
 * interacting with the Spotify API within an OSGi environment.
 */
@ObjectClassDefinition(
        name = "Spotify Integration Configuration",
        description = "Configuration for Spotify Integration Service"
)
public @interface SpotifyIntegrationConfig {
    /**
     * Retrieves the Spotify App Client ID used for authentication.
     *
     * @return The client ID as a string, with a default empty string if not set
     */
    @AttributeDefinition(
            name = "Client ID",
            description = "Spotify App Client ID"
    )
    String clientId() default "";

    /**
     * Retrieves the Spotify App Client Secret used for authentication.
     * @return The client secret as a string, with a default empty string if not set
     */
    @AttributeDefinition(
            name = "Client Secret",
            description = "Spotify App Client Secret"
    )
    String clientSecret() default "";

    /**
     * Retrieves the redirect URI used during Spotify OAuth authentication process.
     * @return The redirect URI as a string, defaulting to a localhost AEM path
     */
    @AttributeDefinition(
            name = "Redirect URI",
            description = "Redirect URI for Spotify authentication"
    )
    String redirectUri() default "http://localhost:4502/content/assignment/spotifycheck/jcr:content/root/spotify";

    /**
     * Retrieves the base URL for Spotify authentication endpoints.
     * @return The Spotify authentication base URL as a string
     */
    @AttributeDefinition(
            name = "Spotify API Base URL",
            description = "Base URL for Spotify authentication"
    )
    String spotifyApiBaseUrl() default "https://accounts.spotify.com";

    /**
     * Retrieves the URL for fetching a user's liked songs from Spotify.
     * @return The Spotify liked songs endpoint URL as a string
     */
    @AttributeDefinition(
            name = "Liked Songs URL",
            description = "Spotify URL for liked songs"
    )
    String spotifyLikedSongsUrl() default "https://api.spotify.com/v1/me/tracks";
}

