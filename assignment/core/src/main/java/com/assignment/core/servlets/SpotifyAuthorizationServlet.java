package com.assignment.core.servlets;

import com.assignment.core.services.SpotifyIntegrationService;
import com.assignment.core.services.SpotifyDataProcessingService;
import com.assignment.core.services.SpotifyNodeStorageService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.*;
import javax.servlet.Servlet;
import javax.servlet.http.Cookie;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Map;

/**
 * Servlet for handling Spotify authorization and token management.
 * Manages OAuth2 flow, token retrieval, and initial data processing.
 */
@Component(
        service = Servlet.class,
        property = {
                ServletResolverConstants.SLING_SERVLET_RESOURCE_TYPES + "=assignment/components/spotify",
                ServletResolverConstants.SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_GET
        }
)
public class SpotifyAuthorizationServlet extends SlingSafeMethodsServlet {

    private static final Logger log = LoggerFactory.getLogger(SpotifyAuthorizationServlet.class);

    @Reference
    private SpotifyIntegrationService spotifyService;

    @Reference
    private SpotifyDataProcessingService dataProcessingService;

    @Reference
    private SpotifyNodeStorageService nodeStorageService;

    /**
     * Handles GET requests for Spotify authorization and token retrieval.
     *
     * @param request The Sling HTTP servlet request
     * @param response The Sling HTTP servlet response
     * @throws IOException If an error occurs during processing
     */
    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        try {
            String code = request.getParameter("code");

            if (code == null) {
                // Redirect to Spotify authorization URL if no code is provided
                response.sendRedirect(spotifyService.getAuthorizationUrl());
                return;
            }

            Cookie[] cookies = request.getCookies();
            String tokenResponse;

            // Check if we need to refresh the token
            if (shouldRefreshToken(cookies)) {
                String refreshToken = getCookieValue(cookies, "spotifyRefreshToken");
                tokenResponse = spotifyService.refreshAccessToken(refreshToken);
            } else {
                tokenResponse = spotifyService.getAccessToken(code);
            }

            // Handle the tokens and process the data
            handleTokensAndProcessData(tokenResponse, response);

        } catch (IOException e) {
            log.error("Spotify authorization error", e);
            sendErrorResponse(response, e.getMessage());
        }
    }

    /**
     * Determines if the current token needs to be refreshed.
     *
     * @param cookies Array of cookies from the request
     * @return boolean indicating if token refresh is required
     */
    private boolean shouldRefreshToken(Cookie[] cookies) {
        if (cookies == null) return false;

        String expirationTime = getCookieValue(cookies, "tokenExpiration");
        return expirationTime != null && System.currentTimeMillis() > Long.parseLong(expirationTime);
    }

    /**
     * Processes tokens, stores them in cookies, and fetches/processes Spotify data.
     *
     * @param tokenResponse JSON string containing token information
     * @param response Servlet response to send cookies and processed data
     * @throws IOException If an error occurs during processing
     */

    private void handleTokensAndProcessData(String tokenResponse, SlingHttpServletResponse response) throws IOException {
        JsonObject tokens = Json.createReader(new StringReader(tokenResponse)).readObject();

        // Extract access token and expiration time from Spotify response
        String accessToken = tokens.getString("access_token");
        int expiresIn = tokens.getInt("expires_in"); // Expires in seconds
        long expirationTimeInSeconds = expiresIn; // Store expiration time directly in seconds

        // Store access token and expiration time in cookies (in seconds)
        storeCookie(response, "spotifyAccessToken", accessToken, expiresIn);
        storeCookie(response, "tokenExpiration", String.valueOf(expirationTimeInSeconds), expiresIn);

        // Store refresh token if available
        if (tokens.containsKey("refresh_token")) {
            storeCookie(response, "spotifyRefreshToken", tokens.getString("refresh_token"), 30 * 24 * 3600); // 30 days in seconds
        }

        // Fetch and process the user's liked songs from Spotify
        String songsResponse = spotifyService.getLikedSongs(accessToken);

        // Process the data
        Map<String, List<JsonObject>> filteredSongs = dataProcessingService.filterSongsByTimeRange(songsResponse);
        Map<String, List<JsonObject>> songsByArtist = dataProcessingService.groupSongsByArtist(songsResponse);

        // Store the processed data in AEM nodes
        nodeStorageService.storeSongMetadata(filteredSongs, songsByArtist);

        // Prepare and send the response with the processed data
        JsonObjectBuilder processedData = Json.createObjectBuilder();
        processedData.add("rawSongs", Json.createReader(new StringReader(songsResponse)).readObject());
        processedData.add("filteredSongs", convertMapToJson(filteredSongs));
        processedData.add("songsByArtist", convertMapToJson(songsByArtist));

        sendJsonResponse(response, processedData.build().toString());
    }

    /**
     * Stores a cookie with the specified parameters.
     *
     * @param response Servlet response to add the cookie
     * @param name Name of the cookie
     * @param value Value of the cookie
     * @param maxAge Maximum age of the cookie in seconds
     */
    private void storeCookie(SlingHttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(maxAge); // maxAge in seconds
        cookie.setPath("/"); // Cookie valid for the entire domain
        cookie.setSecure(true); // Set to true if your site uses HTTPS
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
    }

    /**
     * Retrieves a cookie's value by its name.
     *
     * @param cookies Array of cookies to search
     * @param name Name of the cookie to find
     * @return Value of the cookie or null if not found
     */
    private String getCookieValue(Cookie[] cookies, String name) {
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    /**
     * Converts a map of songs to a JSON object.
     *
     * @param map Map of song lists to convert
     * @return JsonObject representation of the map
     */
    private JsonObject convertMapToJson(Map<String, List<JsonObject>> map) {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        map.forEach((key, value) -> {
            JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
            value.forEach(arrayBuilder::add);
            builder.add(key, arrayBuilder);
        });
        return builder.build();
    }

    /**
     * Sends a JSON response.
     *
     * @param response Servlet response to write to
     * @param json JSON string to send
     * @throws IOException If an error occurs while writing the response
     */
    private void sendJsonResponse(SlingHttpServletResponse response, String json) throws IOException {
        response.setContentType("application/json");
        response.getWriter().write(json);
    }

    /**
     * Sends an error response.
     *
     * @param response Servlet response to write to
     * @param message Error message to include
     * @throws IOException If an error occurs while writing the response
     */
    private void sendErrorResponse(SlingHttpServletResponse response, String message) throws IOException {
        response.setStatus(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        JsonObject error = Json.createObjectBuilder()
                .add("error", true)
                .add("message", message)
                .build();
        sendJsonResponse(response, error.toString());
    }
}
