package com.assignment.core.service.impl;

import com.assignment.core.service.PageCreationService;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import java.util.UUID;

@Component(service = PageCreationService.class)
public class PageCreationServiceImpl implements PageCreationService {

    /**
     * Creates a new page with the provided details.
     */
    @Override
    public void createPage(String path, String title, String description, String tags, ResourceResolver resolver) throws Exception {
        PageManager pageManager = resolver.adaptTo(PageManager.class);

        if (pageManager != null) {
            String uniquePageName = "page-" + UUID.randomUUID().toString();
            String templatePath = "/conf/assignment/settings/wcm/templates/homeloantemplate";
            Page newPage = pageManager.create(path, uniquePageName, templatePath, title);
            if (newPage != null) {
                Resource pageContentResource = newPage.adaptTo(Resource.class);
                if (pageContentResource != null) {
                    ModifiableValueMap properties = pageContentResource.adaptTo(ModifiableValueMap.class);
                    properties.put("jcr:description", description);
                    properties.put("cq:tags", tags);
                    resolver.commit();
                }
            }
        }
    }
}
