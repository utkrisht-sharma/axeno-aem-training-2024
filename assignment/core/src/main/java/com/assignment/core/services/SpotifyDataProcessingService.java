package com.assignment.core.services;
import javax.json.JsonObject;
import java.util.List;
import java.util.Map;

/**
 * Service interface for processing Spotify song data.
 * Provides methods to filter and group songs based on various criteria.
 */
public interface SpotifyDataProcessingService {

     /**
      * Filters songs based on a specific time range.
      *
      * @param songsResponse JSON string of raw song data
      * @return Map of filtered songs grouped by a time-based key
      */
     Map<String, List<JsonObject>> filterSongsByTimeRange(String songsResponse);

     /**
      * Groups songs by artist name.
      *
      * @param songsResponse JSON string of raw song data
      * @return Map of songs grouped by artist
      */
     Map<String, List<JsonObject>> groupSongsByArtist(String songsResponse);
}