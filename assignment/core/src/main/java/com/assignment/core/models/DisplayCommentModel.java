package com.assignment.core.models;

import com.assignment.core.api.UserPostData;
import com.assignment.core.services.DisplayCommentService;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.util.List;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class DisplayCommentModel {

    private static final Logger LOG = LoggerFactory.getLogger(DisplayCommentModel.class);

    @OSGiService
    private DisplayCommentService displayCommentService;

    @ValueMapValue
    private String title;

    private List<UserPostData> displayComments;

    @PostConstruct
    protected void init() {

            // Fetch post data using the DisplayCommentService
            displayComments = displayCommentService.getPostData();

            // Log the result
            LOG.info("Successfully fetched {} comments", displayComments.size());

    }

    public List<UserPostData> getDisplayComments() {
        return displayComments;
    }

    public String getTitle() {
        return title;
    }
}
