package com.assignment.core.utils;

import org.apache.sling.api.SlingHttpServletResponse;
import org.json.JSONObject;
import java.io.IOException;
import java.util.List;

/**
 * Utility class for handling servlet responses.
 */
public class ServletUtils {

    /**
     * Sends a JSON response containing a list of top 10 paths
     * and the total number of matches.
     */
    public static void sendJsonResponse(SlingHttpServletResponse response, List<String> top10Paths, long totalMatches) throws IOException {
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("top10Paths", top10Paths);
        jsonResponse.put("totalMatches", totalMatches);
        response.setContentType("application/json");
        response.getWriter().write(jsonResponse.toString());
    }

    /**
     * Sends an error response with a specified status code and error message.
     */
    public static void sendErrorResponse(SlingHttpServletResponse response, int statusCode, String message) throws IOException {
        response.setStatus(statusCode);
        response.setContentType("application/json");
        response.getWriter().write(new JSONObject().put("error", message).toString());
    }
}
