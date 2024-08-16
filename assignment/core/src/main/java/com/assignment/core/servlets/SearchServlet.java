package com.assignment.core.servlets;

import com.assignment.core.services.NodeService;
import com.assignment.core.services.SearchService;
import com.assignment.core.utils.ServletUtils;
import com.assignment.core.utils.ValidationUtils;
import com.day.cq.search.result.SearchResult;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;

/**
 * A servlet that handles POST requests for searching and managing nodes.
 * It performs a search based on provided parameters, processes results, and handles
 * the save or deletion of nodes based on the request parameters.
 */
@Component(
        service = {Servlet.class},
        property = {
                "sling.servlet.methods=POST",
                "sling.servlet.paths=/bin/myproject/search",
                "sling.servlet.extensions=json"
        }
)
public class SearchServlet extends SlingAllMethodsServlet {

    private static final Logger LOG = LoggerFactory.getLogger(SearchServlet.class);

    @Reference
    private transient SearchService searchService;

    @Reference
    private transient NodeService nodeService;

    /**
     * Handles POST requests to search for pages and manage nodes.
     */
    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        LOG.info("Processing POST request at /bin/myproject/search");

        try {
            String path = ValidationUtils.validateParam(request.getParameter("path"));
            String propertyOne = ValidationUtils.validateParam(request.getParameter("propertyOne"));
            String propertyOneValue = ValidationUtils.validateParam(request.getParameter("propertyOneValue"));
            String propertyTwo = ValidationUtils.validateParam(request.getParameter("propertyTwo"));
            String propertyTwoValue = ValidationUtils.validateParam(request.getParameter("propertyTwoValue"));
            boolean save = Boolean.parseBoolean(request.getParameter("save"));

            LOG.debug("Parameters extracted: path={}, propertyOne={}, propertyOneValue={}, propertyTwo={}, propertyTwoValue={}, save={}",
                    path, propertyOne, propertyOneValue, propertyTwo, propertyTwoValue, save);

            SearchResult searchResult = searchService.searchPages(path, propertyOne, propertyOneValue, propertyTwo, propertyTwoValue);
            long totalMatches = searchResult.getTotalMatches();
            LOG.info("Search completed with {} total matches", totalMatches);

            if (save) {
                LOG.info("Saving search results with total matches: {}", totalMatches);
                nodeService.saveSearchResults(totalMatches);
            } else {
                LOG.info("Deleting saved nodes");
                nodeService.deleteSavedNodes();
            }
            ServletUtils.sendJsonResponse(response, searchService.getTop10Paths(searchResult), totalMatches);
        } catch (IllegalArgumentException e) {
            LOG.warn("Validation failed: {}", e.getMessage());
            ServletUtils.sendErrorResponse(response, SlingHttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            LOG.error("Error processing search request", e);
            ServletUtils.sendErrorResponse(response, SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred. Please try again.");
        }
    }
}
