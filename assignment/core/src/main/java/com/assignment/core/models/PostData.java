package com.assignment.core.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PostData {
    @JsonProperty("userId")
    String userId;
    @JsonProperty("id")
    String id;
    @JsonProperty("title")
    String title;
    @JsonProperty("body")
    String body;

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }
}
