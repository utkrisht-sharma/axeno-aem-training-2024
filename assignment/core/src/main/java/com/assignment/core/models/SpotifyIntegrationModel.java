package com.assignment.core.models;

import com.assignment.core.services.SpotifyIntegrationService;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;

/**
 * Model to interact with Spotify's authentication system.
 * This model uses the SpotifyIntegrationService to fetch the Spotify authorization URL.
 * It is designed to be used with a resource in Sling and supports optional injection strategy for flexibility.
 */
 @Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class SpotifyIntegrationModel {

    @OSGiService
    private SpotifyIntegrationService spotifyIntegrationService;

    /**
     * Returns the Spotify authorization URL used for initiating the OAuth2 authentication process.
     * <p>
     * The authorization URL is required to start the Spotify login flow and receive an authorization
     * code that can be exchanged for an access token.
     * </p>
            *
            * @return the Spotify authorization URL as a String.
     */
    public String getAuthorizationUrl() {
        return spotifyIntegrationService.getAuthorizationUrl();
    }
}
