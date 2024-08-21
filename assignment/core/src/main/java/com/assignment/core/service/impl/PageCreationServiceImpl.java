package com.assignment.core.service.impl;

import com.assignment.core.service.PageCreationService;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import java.util.Objects;
import java.util.UUID;


@Component(service = PageCreationService.class)
public class PageCreationServiceImpl implements PageCreationService {
    private static final Logger log = LoggerFactory.getLogger(PageCreationServiceImpl.class);

    /**
     * Creates a new page with the provided details.
     */
    @Override
    public void createPage(String path, String title, String description, String tags, String thumbnail, ResourceResolver resolver) throws Exception {
        log.info("Creation Of Page Started");
        PageManager pageManager = resolver.adaptTo(PageManager.class);
        boolean pageCreated = true;
        if (!Objects.isNull(pageManager)) {
            log.info("PageManager Is Not Null. ");
            String uniquePageName = "page-" + UUID.randomUUID().toString();;
            String templatePath = "/conf/assignment/settings/wcm/templates/homeloantemplate";
            Page newPage = pageManager.create(path, uniquePageName, templatePath, title);
            if (!Objects.isNull(newPage)) {
                log.info("New Page Is Not Null. ");
                Node pageContentResource = newPage.getContentResource().adaptTo(Node.class);
                if (!Objects.isNull(pageContentResource)) {
                    log.info("Setting Page Properties ");
                    pageContentResource.setProperty("jcr:description", description);
                    pageContentResource.setProperty("cq:tags", tags);
                    pageContentResource.setProperty("cq:thumbnail", thumbnail);
                    log.info("Page Properties Set Successfully. ");
                } else {
                    log.error("Page Content Resource Is Null. ");
                    pageCreated = false;
                }
            } else {
                log.error("New Page Is Null. ");
                pageCreated = false;
            }
        } else {
            log.error("Page Manager Is Null. ");
            pageCreated = false;
        }

        if (pageCreated) {
            log.info(title + " Page Created Successfully. ");
        }
    }
}
