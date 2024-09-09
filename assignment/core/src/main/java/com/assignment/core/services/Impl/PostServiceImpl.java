package com.assignment.core.services.Impl;

import com.assignment.core.models.Post;
import com.assignment.core.services.PostsService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URISyntaxException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the PostsService interface.
 */
@Component(service = PostsService.class, immediate = true)
public class PostServiceImpl implements PostsService {

    private static final Logger LOG = LoggerFactory.getLogger(PostServiceImpl.class);
    private static final String API_URL = "https://jsonplaceholder.typicode.com/posts";
    private final HttpClient httpClient = HttpClient.newHttpClient();
/**
 * Retrieves a list of posts from a remote API.
 * This method sends an HTTP GET request to the API endpoint, processes the JSON response
 */
    @Override
    public List<Post> getPosts() {
        List<Post> posts = new ArrayList<>();
        LOG.info("Starting the process of fetching posts");

        try {
            HttpRequest request = HttpRequest.newBuilder().uri(new URI(API_URL)).GET().build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            String jsonResponse = response.body();
            JSONArray jsonArray = new JSONArray(jsonResponse);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Post post = new Post(jsonObject.getString("title"), jsonObject.getString("body"));
                posts.add(post);
            }
        } catch (URISyntaxException e) {
            LOG.error("Invalid URI syntax: {}", e.getMessage());
        } catch (IOException e) {
            LOG.error("I/O error occurred while fetching posts from the API", e);
        } catch (InterruptedException e) {
            LOG.error("The HTTP request was interrupted", e);
        } catch (Exception e) {
            LOG.error("An unexpected error occurred while fetching posts from the API", e);
        }
        LOG.info("Completed fetching posts from the API");
        return posts;
    }
}
