package com.assignment.core.services.impl;

import com.assignment.core.models.RequestParameters;
import com.assignment.core.services.SearchService;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.sling.api.resource.*;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component(service = SearchService.class)
public class SearchServiceImpl implements SearchService {

    private static final Logger log = LoggerFactory.getLogger(SearchServiceImpl.class);

    @Reference
    private QueryBuilder queryBuilder;

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Override
    public Pair<List<String>, Integer> searchPages(RequestParameters params) throws LoginException, RepositoryException {
        List<String> resultPaths = new ArrayList<>();
        int totalMatches = 0;

        try (ResourceResolver resourceResolver = resourceResolverFactory.getServiceResourceResolver(createServiceUserMap())) {
            log.info("Starting search the pages..");

            // Query for total matches
            Map<String, String> totalQueryMap = createQueryMap(params);
            totalQueryMap.put("p.limit", "0");

            Query totalQuery = queryBuilder.createQuery(PredicateGroup.create(totalQueryMap), resourceResolver.adaptTo(Session.class));
            totalQuery.setHitsPerPage(0);
            SearchResult totalSearchResult = totalQuery.getResult();
            totalMatches = totalSearchResult.getHits().size();
            log.info("Total matches found: {}", totalMatches);

            // Query for top 10 results
            Map<String, String> queryMap = new HashMap<>(totalQueryMap);
            queryMap.put("orderby", "@jcr:content/cq:lastModified");
            queryMap.put("orderby.sort", "desc");
            queryMap.put("p.limit", "10");

            Query query = queryBuilder.createQuery(PredicateGroup.create(queryMap), resourceResolver.adaptTo(Session.class));
            SearchResult searchResult = query.getResult();

            for (Hit hit : searchResult.getHits()) {
                resultPaths.add(hit.getPath());
                log.info("Found result: {}", hit.getPath());
            }
        }
        return Pair.of(resultPaths, totalMatches);
    }


    private Map<String, String> createQueryMap(RequestParameters params) {
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("path", params.getSearchPath());
        queryMap.put("type", "cq:Page");
        queryMap.put("1_property", "jcr:content/" + params.getPropertyOne());
        queryMap.put("1_property.value", params.getPropertyOneValue());
        queryMap.put("2_property", "jcr:content/" + params.getPropertyTwo());
        queryMap.put("2_property.value", params.getPropertyTwoValue());
        queryMap.put("3_daterange.property", "jcr:content/cq:lastModified");
        queryMap.put("3_daterange.lowerBound", "2018-01-01T00:00:00.000Z");
        queryMap.put("3_daterange.upperBound", "2020-12-31T23:59:59.999Z");
        return queryMap;
    }

    private Map<String, Object> createServiceUserMap() {
        Map<String, Object> serviceUserMap = new HashMap<>();
        serviceUserMap.put(ResourceResolverFactory.SUBSERVICE, "datawriteservice");
        return serviceUserMap;
    }
}
