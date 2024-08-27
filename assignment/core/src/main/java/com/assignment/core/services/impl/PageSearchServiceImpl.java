package com.assignment.core.services.impl;


import com.assignment.core.services.PageSearchService;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;


import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.HashMap;
import java.util.List;
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
     * {@inheritDoc}
     */
    @Override
    public JSONObject findPages(ResourceResolver resolver, Map<String,String>parameters) throws RepositoryException {
        LOG.info("Searching Of Page Started");
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("path", parameters.get("path"));
        queryMap.put("type", "cq:Page");
        queryMap.put("1_property", "jcr:content/" + parameters.get("propertyOne"));
        queryMap.put("1_property.value", parameters.get("propertyOneValue"));
        queryMap.put("2_property", "jcr:content/" + parameters.get("propertyTwo"));
        queryMap.put("2_property.value", parameters.get("propertyTwoValue"));
        queryMap.put("p.limit", "10");
        queryMap.put("orderby", "@jcr:content/jcr:lastModified");
        queryMap.put("orderby.sort", "desc");
        queryMap.put("range.property", "jcr:content/jcr:lastModified");
        queryMap.put("range.lowerBound", "2018-01-01T00:00:00.000Z");
        queryMap.put("range.upperBound", "2020-12-31T23:59:59.999Z");
        PredicateGroup predicateGroup = PredicateGroup.create(queryMap);
        Query query = queryBuilder.createQuery(predicateGroup, resolver.adaptTo(Session.class));
        LOG.info("Searching completed Successfully");
        return getPathsFromSearchResults(query.getResult());


    }

    /**
     *{@inheritDoc}
     */
    @Override
    public JSONObject getPathsFromSearchResults(SearchResult result) throws RepositoryException {
        JSONObject jsonResponse = new JSONObject();
        JSONArray resultsArray = new JSONArray();

        List<Hit> hits = result.getHits();
        for (Hit hit : hits) {
            resultsArray.put(hit.getPath());
        }
        jsonResponse.put("totalMatches", result.getTotalMatches());
        jsonResponse.put("topResults", resultsArray);
        return jsonResponse;
    }


}
