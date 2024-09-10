package com.assignment.core.models;

import com.assignment.core.api.UserPostData;

import com.assignment.core.services.DisplayUserpostService;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.util.List;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class DisplayUserpostModel {

    private static final Logger LOG = LoggerFactory.getLogger(DisplayUserpostModel.class);

    @OSGiService
    private DisplayUserpostService displayUserpostService;



    private List<UserPostData> displayUserposts;

    @PostConstruct
    protected void init() {

            // Fetch post data using the DisplayCommentService
            displayUserposts = displayUserpostService.getPostData();

            // Log the result
            LOG.info("Successfully fetched {} comments", displayUserposts.size());

    }

    public List<UserPostData> getDisplayComments() {
        return displayUserposts;
    }


}
