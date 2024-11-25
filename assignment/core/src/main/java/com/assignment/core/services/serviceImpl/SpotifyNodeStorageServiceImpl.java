package com.assignment.core.services.serviceImpl;

import com.assignment.core.services.SpotifyNodeStorageService;
import org.apache.sling.api.resource.*;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.JsonObject;
import java.util.*;

/**
 * Implementation of the SpotifyNodeStorageService interface. This service is responsible for storing and updating song metadata,
 * including filtering songs based on certain criteria and categorizing them by artist.
 * The implementation stores this data in the AEM repository under the "spotify-data" node.
 */

@Component(service = SpotifyNodeStorageService.class, immediate = true)
public class SpotifyNodeStorageServiceImpl implements SpotifyNodeStorageService {

    private static final Logger log = LoggerFactory.getLogger(SpotifyNodeStorageServiceImpl.class);

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    private static final String serviceUser = "spotifyuser";  // Hardcoded service user
    private static final String basePath = "/content/assignment";  // Hardcoded base path

    /**
     * Stores or updates song metadata in the repository. This method stores filtered songs and categorizes songs by artist.
     * It creates or updates nodes in the AEM repository under the "spotify-data" node.
     *
     * @param filteredSongs A map of filtered songs categorized by timeframes.
     * @param songsByArtist A map of songs categorized by artist.
     */

    @Override
    public void storeSongMetadata(Map<String, List<JsonObject>> filteredSongs,
                                  Map<String, List<JsonObject>> songsByArtist) {
        ResourceResolver resolver = null;

        try {
            // Create a simple HashMap for authentication
            Map<String, Object> authenticationParameters = new HashMap<>();
            authenticationParameters.put(ResourceResolverFactory.SUBSERVICE, serviceUser);

            // Get resource resolver
            resolver = resourceResolverFactory.getServiceResourceResolver(authenticationParameters);

            // Check if root path exists
            String rootPath = basePath + "/spotify-data";
            Resource rootResource = resolver.getResource(rootPath);

            // If root doesn't exist, create it
            if (rootResource == null) {
                log.info("Creating root path for Spotify data at: {}", rootPath);
                rootResource = createInitialResource(resolver, rootPath);
            }

            // Store or update filtered songs
            storeOrUpdateFilteredSongs(resolver, rootResource, filteredSongs);

            // Store or update songs by artist
            storeOrUpdateArtistSongs(resolver, rootResource, songsByArtist);

            // Save changes only if there are any
            if (resolver.hasChanges()) {
                resolver.commit();
                log.info("Successfully saved updated song data");
            } else {
                log.info("No changes detected, skipping save");
            }

        } catch (PersistenceException | LoginException e) {
            log.error("Error while storing songs in storeSongMetadata method: {}", e.getMessage(), e);
        } finally {
            if (resolver != null && resolver.isLive()) {
                resolver.close();
            }
        }
    }

    /** creates Intital resource
    * and add basic properties needed for node
     */

    private Resource createInitialResource(ResourceResolver resolver, String path) {
        // Create basic properties for new node
        Map<String, Object> properties = new HashMap<>();
        properties.put("jcr:primaryType", "nt:unstructured");
        properties.put("created", Calendar.getInstance());
        return createResource(resolver, path, properties);
    }

    /**
     * this method is used for storing or updating filtered songs

     * @param resolver The resource resolver.
     * @param rootResource The root resource to store data under.
     * @param filteredSongs The map of filtered songs.
     * @throws PersistenceException If an error occurs during the persistence operation.
     */
    private void storeOrUpdateFilteredSongs(ResourceResolver resolver, Resource rootResource,
                                            Map<String, List<JsonObject>> filteredSongs) throws PersistenceException {

        //create filtered-songs node
        String filteredPath = rootResource.getPath() + "/filtered-songs";
        Resource filteredResource = resolver.getResource(filteredPath);

        if (filteredResource == null) {
            log.info("Filtered songs node not found. Creating new node at path: {}", filteredPath);
            Map<String, Object> properties = new HashMap<>();
            properties.put("jcr:primaryType", "nt:unstructured");
            filteredResource = resolver.create(rootResource, "filtered-songs", properties);
        }

        // Process each timeframe
        for (Map.Entry<String, List<JsonObject>> entry : filteredSongs.entrySet()) {
            String timeframe = entry.getKey();
            List<JsonObject> songs = entry.getValue();

            String timeframePath = filteredResource.getPath() + "/" + makeValidNodeName(timeframe);
            Resource timeframeResource = resolver.getResource(timeframePath);

            // Convert songs to string for comparison
            String newSongsData = songs.toString();

            if (timeframeResource != null) {
                // Node exists - check if data is different
                ValueMap existingproperties = timeframeResource.getValueMap();
                String existingSongs = existingproperties.get("songs", String.class);

                if (!newSongsData.equals(existingSongs)) {
                    // Update only if data has changed
                    ModifiableValueMap properties = timeframeResource.adaptTo(ModifiableValueMap.class);
                    if (properties != null) {
                        properties.put("songs", newSongsData);
                        properties.put("lastModified", Calendar.getInstance());
                        log.info("Updated existing timeframe data: {}", timeframe);
                    } else {
                        log.error("Failed to adapt timeframe resource to ModifiableValueMap for timeframe: {}", timeframe);
                    }
                } else {
                    log.debug("No changes for timeframe: {}", timeframe);
                }
            } else {
                // Create new node with data
                Map<String, Object> properties = new HashMap<>();
                properties.put("jcr:primaryType", "nt:unstructured");
                properties.put("songs", newSongsData);
                properties.put("created", Calendar.getInstance());
                resolver.create(filteredResource, makeValidNodeName(timeframe), properties);
                log.info("Created new timeframe data: {}", timeframe);
            }
        }
    }

    /**
     * this method is used for storing or updating Artist songs
     *  @param resolver The resource resolver.
     *  @param rootResource The root resource to store data under.
     *  @param songsByArtist The map of songs categorized by artist.
     */

    private void storeOrUpdateArtistSongs(ResourceResolver resolver, Resource rootResource,
                                          Map<String, List<JsonObject>> songsByArtist)  {

        // Get or create artists node
        String artistsPath = rootResource.getPath() + "/artists";
        Resource artistsResource = resolver.getResource(artistsPath);

        if (artistsResource == null) {
            log.info("Artists node not found. Creating new node at path: {}", artistsPath);
            Map<String, Object> properties = new HashMap<>();
            properties.put("jcr:primaryType", "nt:unstructured");
            try {
                artistsResource = resolver.create(rootResource, "artists", properties);
            } catch (PersistenceException e) {
                log.error("Failed to create artists node: {}", e.getMessage(), e);
                throw new RuntimeException("Failed to create artists node", e);
            }
        }

        // Process each artist
        for (Map.Entry<String, List<JsonObject>> entry : songsByArtist.entrySet()) {
            String artistName = entry.getKey();
            List<JsonObject> songs = entry.getValue();

            String artistPath = artistsResource.getPath() + "/" + makeValidNodeName(artistName);
            Resource artistResource = resolver.getResource(artistPath);

            // Convert songs to string for comparison
            String newSongsData = songs.toString();

            if (artistResource != null) {
                // Node exists - check if data is different
                ValueMap existingproperties = artistResource.getValueMap();
                String existingSongs = existingproperties.get("songs", String.class);

                if (!newSongsData.equals(existingSongs)) {
                    // Update only if data has changed
                    ModifiableValueMap properties = artistResource.adaptTo(ModifiableValueMap.class);
                    if (properties != null) {
                        properties.put("songs", newSongsData);
                        properties.put("lastModified", Calendar.getInstance());
                        log.info("Updated existing artist data: {}", artistName);
                    } else {
                        log.error("Failed to adapt artist resource to ModifiableValueMap for artist: {}", artistName);
                    }
                } else {
                    log.debug("No changes for artist: {}", artistName);
                }
            } else {
                // Create new node with data
                Map<String, Object> properties = new HashMap<>();
                properties.put("jcr:primaryType", "nt:unstructured");
                properties.put("songs", newSongsData);
                properties.put("created", Calendar.getInstance());
                try {
                    resolver.create(artistsResource, makeValidNodeName(artistName), properties);
                } catch (PersistenceException e) {
                    log.error("Failed to create artist node for {}: {}", artistName, e.getMessage(), e);
                    throw new RuntimeException("Failed to create artist node", e);
                }
                log.info("Created new artist data: {}", artistName);
            }
        }
    }

    /**
     * Helper method to create a new resource.
     *
     * @param resolver The resource resolver.
     * @param path The path where the resource will be created.
     * @param properties The properties to set on the resource.
     * @return The created resource.
     */

    private Resource createResource(ResourceResolver resolver, String path, Map<String, Object> properties) {
        try {
            int lastSlash = path.lastIndexOf('/');
            String parentPath = path.substring(0, lastSlash);
            String nodeName = path.substring(lastSlash + 1);

            // Use HashMap instead of singletonMap
            Map<String, Object> parentProperties = new HashMap<>();
            parentProperties.put("jcr:primaryType", "nt:unstructured");  // Same key-value as before

            Resource parentResource = resolver.getResource(parentPath);
            if (parentResource == null) {
                parentResource = resolver.create(resolver.getResource(parentPath), "nt:unstructured", parentProperties);
            }

            return resolver.create(parentResource, nodeName, properties);
        } catch (PersistenceException e) {
            log.error("Error creating resource at {}: {}", path, e.getMessage(), e);
            throw new RuntimeException("Failed to create resource", e);
        }
    }

    /**
     * Ensures the node name is valid by replacing any invalid characters.
     *
     * @param name The name to be converted to a valid node name.
     * @return A valid node name.
     */

    private String makeValidNodeName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "unnamed_" + System.currentTimeMillis();
        }

        return name.replaceAll("[^a-zA-Z0-9-_]", "-")
                .toLowerCase()
                .replaceAll("^[^a-z]", "n$0");
    }
}
