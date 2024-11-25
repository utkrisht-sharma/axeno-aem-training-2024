package com.assignment.core.services.serviceImpl;

import com.assignment.core.services.SpotifyDataProcessingService;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.json.*;
import javax.json.stream.JsonParsingException;
import java.io.StringReader;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Implementation of SpotifyDataProcessingService to process and filter Spotify song data.
 * Provides methods to categorize songs by time range and group songs by artist.
 */
@Component(service = SpotifyDataProcessingService.class, immediate = true)
public class SpotifyDataProcessingServiceImpl implements SpotifyDataProcessingService {

    private static final Logger logger = LoggerFactory.getLogger(SpotifyDataProcessingServiceImpl.class);

    /**
     * Filters songs from the Spotify response based on different time ranges.
     *
     * @param jsonResponse Raw JSON response from Spotify containing liked songs
     * @return Map of songs categorized by time ranges: oneMonthAgo, threeMonthsAgo, sixMonthsAgo
     */

    @Override
    public Map<String, List<JsonObject>> filterSongsByTimeRange(String jsonResponse) {
        Map<String, List<JsonObject>> filteredSongs = new HashMap<>();
        filteredSongs.put("oneMonthAgo", new ArrayList<>());
        filteredSongs.put("threeMonthsAgo", new ArrayList<>());
        filteredSongs.put("sixMonthsAgo", new ArrayList<>());

        try {
            JsonObject response = Json.createReader(new StringReader(jsonResponse)).readObject();
            JsonArray items = response.getJsonArray("items");

            Instant now = Instant.now();

            for (JsonValue itemValue : items) {
                JsonObject item = itemValue.asJsonObject();
                String addedAtString = item.getString("added_at");
                Instant addedAt = Instant.parse(addedAtString);
                long daysAgo = ChronoUnit.DAYS.between(addedAt, now);

                JsonObject track = item.getJsonObject("track");
                JsonObject simplifiedTrack = createSimplifiedTrackMetadata(track);

                if (daysAgo <= 30) {
                    filteredSongs.get("oneMonthAgo").add(simplifiedTrack);
                } else if (daysAgo <= 90) {
                    filteredSongs.get("threeMonthsAgo").add(simplifiedTrack);
                } else if (daysAgo > 90) {
                    filteredSongs.get("sixMonthsAgo").add(simplifiedTrack);
                }
            }
        } catch (JsonParsingException e) {
            logger.error("Error parsing JSON response while filtering songs by time range: {}", e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid argument in the response data: {}", e.getMessage(), e);
        }

        return filteredSongs;
    }

    /**
     * Groups songs by artist from the Spotify response.
     *
     * @param jsonResponse Raw JSON response from Spotify containing liked songs
     * @return Map of songs grouped by artist name
     */

    @Override
    public Map<String, List<JsonObject>> groupSongsByArtist(String jsonResponse) {
        Map<String, List<JsonObject>> songsByArtist = new HashMap<>();

        try {
            JsonObject response = Json.createReader(new StringReader(jsonResponse)).readObject();
            JsonArray items = response.getJsonArray("items");

            for (JsonValue itemValue : items) {
                JsonObject item = itemValue.asJsonObject();
                JsonObject track = item.getJsonObject("track");

                JsonObject simplifiedTrack = createSimplifiedTrackMetadata(track);
                JsonArray artists = track.getJsonArray("artists");

                for (JsonValue artistValue : artists) {
                    String artistName = artistValue.asJsonObject().getString("name");
                    artistName = (artistName == null || artistName.trim().isEmpty()) ? "Unknown Artist" : artistName;

                    JsonObject songAndAlbum = Json.createObjectBuilder()
                            .add("song", simplifiedTrack.getString("name"))
                            .add("album", simplifiedTrack.getString("album"))
                            .build();

                    songsByArtist.computeIfAbsent(artistName, k -> new ArrayList<>()).add(songAndAlbum);
                }
            }
        } catch (JsonParsingException e) {
            logger.error("Error parsing JSON response while grouping songs by artist: {}", e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid argument in the response data: {}", e.getMessage(), e);
        }

        return songsByArtist;
    }

    /**
     * Creates a simplified track metadata object with essential information.
     *
     * @param track Original track JSON object
     * @return Simplified JsonObject with name, artist, and album
     */

    private JsonObject createSimplifiedTrackMetadata(JsonObject track) {
        String artistName = track.getJsonArray("artists").getJsonObject(0).getString("name");

        return Json.createObjectBuilder()
                .add("name", track.getString("name"))
                .add("artist", artistName)
                .add("album", track.getJsonObject("album").getString("name"))
                .build();
    }
}