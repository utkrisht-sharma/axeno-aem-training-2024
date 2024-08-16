package com.assignment.core.services.impl;


import com.assignment.core.services.PageSearchService;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.SearchResult;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.jcr.Session;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of the PageSearchService for finding pages..
 */

@Component(service = PageSearchService.class)
public class PageSearchServiceImpl implements  PageSearchService{

    private static final Logger LOG = LoggerFactory.getLogger(PageSearchServiceImpl.class);

    @Reference
    private QueryBuilder queryBuilder;

    /**
     * Finds pages based on provided search criteria.
     */
    @Override
    public SearchResult findPages(ResourceResolver resolver, String path, String propertyOne, String propertyOneValue, String propertyTwo, String propertyTwoValue) {
        LOG.info("Searching Of Page Started");
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("path", path);
        queryMap.put("type", "cq:Page");
        queryMap.put("1_property", "jcr:content/" + propertyOne);
        queryMap.put("1_property.value", propertyOneValue);
        queryMap.put("2_property", "jcr:content/" + propertyTwo);
        queryMap.put("2_property.value", propertyTwoValue);
        queryMap.put("p.limit", "10");
        queryMap.put("orderby", "@jcr:content/jcr:lastModified");
        queryMap.put("orderby.sort", "desc");
        queryMap.put("range.property", "jcr:content/jcr:lastModified");
        queryMap.put("range.lowerBound", "2018-01-01T00:00:00.000Z");
        queryMap.put("range.upperBound", "2020-12-31T23:59:59.999Z");
        PredicateGroup predicateGroup = PredicateGroup.create(queryMap);
        Query query = queryBuilder.createQuery(predicateGroup, resolver.adaptTo(Session.class));
        LOG.info("Searching completed Successfully");
        return query.getResult();

    }

}
