package com.assignment.core.models;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class BreadCrumbModel {
    private Page currentPage;
    List<String> parents;
    @Self
    private Resource resource;


    @PostConstruct
    protected void init() {
        parents = new ArrayList<>();
        PageManager pageManager = resource.getResourceResolver().adaptTo(PageManager.class);
        currentPage = pageManager.getContainingPage(resource);
        int level = 0;
        while (currentPage != null && level < 3) {
            parents.add(0,currentPage.getTitle());
            currentPage = currentPage.getParent();
            level++;

        }
    }

    public List<String> getParents() {
        return parents;
    }

}
