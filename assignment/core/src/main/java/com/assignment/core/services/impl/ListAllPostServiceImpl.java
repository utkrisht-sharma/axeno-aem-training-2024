package com.assignment.core.services.impl;

import com.assignment.core.models.Post;
import com.assignment.core.services.ListAllPostService;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component(service = ListAllPostService.class, immediate = true)
public class ListAllPostServiceImpl implements ListAllPostService {

    private static final Logger logger = LoggerFactory.getLogger(ListAllPostServiceImpl.class);

    private final String URL = "https://jsonplaceholder.typicode.com/posts";

    private List<Post> posts;

    @Activate
    protected void activate() {
        try {
            this.posts = getPostList();
        } catch (IOException e) {
            logger.error("An error occured while getting the posts : {}", e.getMessage());
        }
    }


    public List<Post> getList() {
        return posts;
    }

    /**
     * making a get request to an api and returning the json response as list.
     *
     * @return list of all posts details
     * @throws IOException
     */
    private List<Post> getPostList() throws IOException {

        logger.info("Fetching posts from URL: {}", URL);
        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
            HttpGet getMethod = new HttpGet(URL);
            HttpResponse response;
            response = httpClient.execute(getMethod);

            // Convert response to string
            String result = EntityUtils.toString(response.getEntity());
            logger.info("response result : {}", result);

            JsonArray jsonArray = JsonParser.parseString(result).getAsJsonArray();
            List<Post> posts = new ArrayList<>();
            for (JsonElement element : jsonArray) {
                Post post = new Gson().fromJson(element, Post.class);
                posts.add(post);
//                logger.info("post : {}", post);
            }
            logger.info("Total posts : {}", posts.size());
            return posts;
        }
    }
}



