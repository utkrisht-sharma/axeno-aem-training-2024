package com.assignment.core.models;

import com.assignment.core.services.PostsService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import java.util.List;
/**
 * A Sling Model that provides access to a list of posts.
 */
@Model(adaptables = SlingHttpServletRequest.class,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class PostsModel {

    @OSGiService
    private PostsService postService;
    /**
     * Retrieves the list of posts from the PostsService.
     */
    public List<Post> getPosts() {
        return postService.getPosts();
    }
}
