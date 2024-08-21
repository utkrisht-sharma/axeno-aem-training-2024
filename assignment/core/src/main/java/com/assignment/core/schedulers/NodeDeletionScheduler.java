package com.assignment.core.schedulers;

import com.assignment.core.config.NodeDeletionSchedulerConfiguration;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.commons.scheduler.ScheduleOptions;
import org.apache.sling.commons.scheduler.Scheduler;
import org.osgi.service.component.annotations.*;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Component(service= Runnable.class , immediate = true)
@Designate(ocd = NodeDeletionSchedulerConfiguration.class)
public class NodeDeletionScheduler implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(NodeDeletionScheduler.class);
    private static final String NODE_PATH = "/content/assignment/new-page-2";  // Path to the node to be deleted

    @Reference
    private Scheduler scheduler;


    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Activate
    @Modified
    public void activate(NodeDeletionSchedulerConfiguration config) {
        if(config.enabled()) {
            ScheduleOptions options = scheduler.EXPR(config.cronExpression());
            options.name(config.schedulerName());
            scheduler.schedule(this, options);
        }else{
            scheduler.unschedule(config.schedulerName());
        }

    }

    @Deactivate
    protected void deactivate(NodeDeletionSchedulerConfiguration config){
        scheduler.unschedule(config.schedulerName());
    }

    @Override
    public void run() {
        Map<String, Object> authInfo = new HashMap<>();
        authInfo.put(ResourceResolverFactory.SUBSERVICE, "nodeservice");

        try (ResourceResolver resourceResolver = resourceResolverFactory.getServiceResourceResolver(authInfo)) {
            Resource resource = resourceResolver.getResource(NODE_PATH);

            if (resource != null) {
                Iterator<Resource> childResources = resource.listChildren();
                if (childResources.hasNext()) {
                    Resource childResource = childResources.next();
                    resourceResolver.delete(childResource);
                    LOG.info("Deleted child node: {}", childResource.getPath());
                }
                else{
                    LOG.warn("Child not found: {}", NODE_PATH);
                }
                resourceResolver.commit();
            } else {
                LOG.warn("Node not found: {}", NODE_PATH);
            }
        } catch (Exception e) {
            LOG.error("Failed to delete node: {}", NODE_PATH, e);
        }
    }
}

