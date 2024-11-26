package com.assignment.core.services;

import javax.json.JsonObject;
import java.util.List;
import java.util.Map;

/**
 * Service interface for storing and updating song metadata, including filtered songs and songs categorized by artist.
 */
public interface SpotifyNodeStorageService {

    /**
     * Stores song metadata in AEM repository.
     *
     * @param filteredSongs Map of songs filtered by time range
     * @param songsByArtist Map of songs grouped by artist
     */
    void storeSongMetadata( Map<String, List<JsonObject>> filteredSongs, Map<String, List<JsonObject>> songsByArtist);
}
