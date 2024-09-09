package com.assignment.core.services;

import com.assignment.core.models.Post;
import java.util.List;
/**
 * Service interface for managing posts.
 */
public interface PostsService {
/**
 * Retrieves a list of all posts.
 */
    List<Post> getPosts();
}
