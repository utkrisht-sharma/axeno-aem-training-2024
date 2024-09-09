package com.assignment.core.models;

import com.assignment.core.services.ListAllPostService;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import javax.annotation.PostConstruct;
import java.util.List;

/**
 *  Component Model to return all posts in a list
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class GetAllPostsModel {

    private List<Post> posts;

    @OSGiService
    ListAllPostService listAllPostService;

    @PostConstruct
    public void init(){
        posts = listAllPostService.getList();
    }

    public List<Post> getPosts() {
        return posts;
    }
}
