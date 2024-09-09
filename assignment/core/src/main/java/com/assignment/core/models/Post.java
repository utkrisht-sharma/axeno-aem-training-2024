package com.assignment.core.models;
/**
  * Represents a post with a title and body.
 */
public class Post {
    private String title;
    private String body;
 /**
   * Constructs a new Post with the specified title and body.
 */
 public Post( String title, String body) {
        this.title = title;
        this.body = body;
    }
   /**
     * Returns the title of the post.
   */
    public String getTitle() {
        return title;
    }
  /**
   * Returns the title of the post.
  */
    public String getBody() {
        return body;
    }
}
