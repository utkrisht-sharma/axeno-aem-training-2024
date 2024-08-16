package com.assignment.core.servlets;

import com.assignment.core.services.NodeOperationService;
import com.assignment.core.services.PageSearchService;
import com.assignment.core.services.ValidationService;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servlet to handle page search and node management operations.
 */
@Component(
        service = { Servlet.class },
        property = {
                "sling.servlet.paths=/bin/pageSearchAndManagement",
                "sling.servlet.methods=POST",
                "sling.servlet.extensions=json"
        }
)
public class SearchPagesServlet extends SlingAllMethodsServlet {
    private static final Logger LOG = LoggerFactory.getLogger(SearchPagesServlet.class);

    @Reference
    PageSearchService pageSearchService;
    @Reference
    ValidationService validation;
    @Reference
    NodeOperationService operations;

    /**
     * Handles POST requests for searching pages and managing nodes.
     */
    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        LOG.info("Handling POST Request");
        PrintWriter writer=response.getWriter();
        String path = request.getParameter("path");
        String propertyOne = request.getParameter("propertyOne");
        String propertyOneValue = request.getParameter("propertyOneValue");
        String propertyTwo = request.getParameter("propertyTwo");
        String propertyTwoValue = request.getParameter("propertyTwoValue");
        String save = request.getParameter("save");
        if(!validation.validateParameters(path,propertyOne,propertyOneValue,propertyTwo,propertyOneValue,save)){
            LOG.warn("Validation failed");
            response.setStatus(SlingHttpServletResponse.SC_BAD_REQUEST);
            writer.write("Invalid Parameters");
            return;
        }
        try (ResourceResolver resolver = request.getResourceResolver()) {
            SearchResult searchResult = pageSearchService.findPages(resolver, path, propertyOne, propertyOneValue, propertyTwo, propertyTwoValue);
            JSONObject jsonResponse = new JSONObject();
            JSONArray resultsArray = new JSONArray();

            List<Hit> hits = searchResult.getHits();
            for (Hit hit : hits) {
                resultsArray.put(hit.getPath());
            }
            operations.selectNodeOperation(resolver,save,searchResult.getTotalMatches());
            jsonResponse.put("totalMatches", searchResult.getTotalMatches());
            jsonResponse.put("topResults", resultsArray);

            response.setContentType("application/json");
            response.getWriter().write(jsonResponse.toString());
            LOG.info("Search completed successfully.");
        } catch (Exception e) {
            LOG.error("Error during search operation");
            response.setStatus(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("An error occurred during the search operation.");
        }
    }
}
