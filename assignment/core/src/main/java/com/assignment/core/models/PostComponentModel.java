package com.assignment.core.models;

import com.assignment.core.services.PostComponentService;
import com.google.gson.JsonElement;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * Creating Model for post component

 */


@Model(adaptables = Resource.class)
public class PostComponentModel {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostComponentModel.class);

    @OSGiService
    private PostComponentService postComponentService;

    private List<Post> posts;

    @PostConstruct
    void init() {
        posts = new ArrayList<>();
        String data = postComponentService.getPosts();

        LOGGER.info("Data fetched: {}", data);

        if (data != null) {
            JsonArray jsonArray = JsonParser.parseString(data).getAsJsonArray();

            for (JsonElement jsonElement : jsonArray) {
                Post post = new Post();

                String title = jsonElement.getAsJsonObject().get("title").getAsString();
                post.setTitle(title);

                String body = jsonElement.getAsJsonObject().get("body").getAsString();
                post.setBody(body);

                posts.add(post);
            }
            LOGGER.info("Total posts fetched: {}", posts.size());
        } else {
            LOGGER.info("No data received from PostComponentService");
        }
    }

    /**
     * getPosts is containing the title and body
     *
     * @return is returning the title and body
     */
    public List<Post> getPosts() {
        return posts;
    }

    /**
     * Created a inner class with getter and ssetter
     */
    public static class Post {
        private String title;
        private String body;

        public String getTitle() {
            return title;
        }
        public void setTitle(String title) {
            this.title = title;
        }
        public String getBody() {
            return body;
        }
        public void setBody(String body) {
            this.body = body;
        }
    }
}
