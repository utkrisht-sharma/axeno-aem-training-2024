package com.assignment.core.services.impl;

import com.assignment.core.services.PageCreationService;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;
import org.apache.sling.api.resource.*;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of PageCreationService for creating pages.
 */
@Component(service = PageCreationService.class)
public class PageCreationServiceImpl implements PageCreationService {

    private static final Logger log = LoggerFactory.getLogger(PageCreationServiceImpl.class);
    private static final String TEMPLATE_PATH = "/conf/global/settings/wcm/templates/page";

    @Override
    public void createPage(PageManager pageManager, ResourceResolver resourceResolver, String path, String pageName, String title, String description, String tags, String thumbnail)
            throws WCMException, PersistenceException {

        Page page = pageManager.create(path, pageName, TEMPLATE_PATH, title);
        if (page != null) {
            log.info("Page created successfully at: {}", page.getPath());
            Resource contentResource = page.getContentResource();
            ModifiableValueMap properties = contentResource.adaptTo(ModifiableValueMap.class);

            if (properties != null) {
                properties.put("jcr:description", description);
                properties.put("cq:tags", tags.split(";"));

                // Creating a node for storing the thumbnail image
                Resource thumbnailResource = resourceResolver.create(contentResource, "thumbnail", ValueMap.EMPTY);
                ModifiableValueMap thumbnailProps = thumbnailResource.adaptTo(ModifiableValueMap.class);
                if (thumbnailProps != null) {
                    thumbnailProps.put("jcr:primaryType", "nt:unstructured");
                    thumbnailProps.put("fileReference", thumbnail);
                    log.info("Thumbnail image set at: {}", thumbnailResource.getPath());
                }
            }
            resourceResolver.commit();
        } else {
            log.error("Failed to create page at path: {}", path);
        }
    }
}
