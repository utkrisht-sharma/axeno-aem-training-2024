package com.assignment.core.PageMetaData;

public class PageData {
    private String title;
    private String path;
    private String description;
    private String tags;
    private String thumbnail;
    public PageData(String title, String path, String description, String tags, String thumbnail) {
        this.title = title;
        this.path = path;
        this.description = description;
        this.tags = tags;
        this.thumbnail = thumbnail;
    }

    public String getTitle() {
        return title;
    }

    public String getPath() {
        return path;
    }

    public String getDescription() {
        return description;
    }

    public String getTags() {
        return tags;
    }

    public String getThumbnail(){ return  thumbnail; }
}
