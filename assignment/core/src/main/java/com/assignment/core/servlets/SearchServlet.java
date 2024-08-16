package com.assignment.core.servlets;

import com.assignment.core.services.NodeManagementService;
import com.assignment.core.services.SearchService;
import com.day.cq.wcm.api.WCMException;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.servlet.Servlet;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Servlet that handles search requests and manages session nodes.
 * This servlet is registered to handle POST requests at the path "/bin/search".
 */
@Component(
        service = Servlet.class,
        immediate = true,
        property = {
                "sling.servlet.methods=POST",
                "sling.servlet.paths=/bin/search"
        }
)
public class SearchServlet extends SlingAllMethodsServlet {

    private static final Logger LOG = LoggerFactory.getLogger(SearchServlet.class);

    @Reference
    private SearchService searchService;

    @Reference
    private NodeManagementService nodeManagementService;

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    /**
     * Handles POST requests for searching pages and managing session nodes.
     *
     * @param request  The SlingHttpServletRequest object
     * @param response The SlingHttpServletResponse object
     * @throws IOException If an input or output error occurs
     */
    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws IOException {

        String path = request.getParameter("path");
        String propertyOne = request.getParameter("propertyOne");
        String propertyOneValue = request.getParameter("propertyOneValue");
        String propertyTwo = request.getParameter("propertyTwo");
        String propertyTwoValue = request.getParameter("propertyTwoValue");
        boolean save = Boolean.parseBoolean(request.getParameter("save"));

        if (isNullOrEmpty(path) || isNullOrEmpty(propertyOne) || isNullOrEmpty(propertyOneValue) ||
                isNullOrEmpty(propertyTwo) || isNullOrEmpty(propertyTwoValue)) {
            response.setStatus(SlingHttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("All parameters are required.");
            return;
        }

        try {
            ResourceResolver resourceResolver = request.getResourceResolver();

            List<String> topPages = searchService.searchPages(resourceResolver, path, propertyOne, propertyOneValue, propertyTwo, propertyTwoValue);
            int totalMatches = topPages.size();

            if (save) {
                nodeManagementService.createOrUpdateSessionNode(resourceResolver, totalMatches);
            } else {
                nodeManagementService.deleteSessionNodes(resourceResolver);
            }

            response.setContentType("application/json");
            response.getWriter().write(createJsonResponse(topPages, totalMatches));

        } catch (RepositoryException e) {
            LOG.error("Error in SearchServlet", e);
            response.setStatus(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("An error occurred: " + e.getMessage());
        }
    }

    /**
     * Checks if a string is null or empty.
     *
     * @param str The string to check
     * @return true if the string is null or empty, false otherwise
     */
    private boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * Creates a JSON response string from the search results.
     *
     * @param topPages     List of top pages found in the search
     * @param totalMatches Total number of matches found
     * @return A JSON string representing the search results
     */
    private String createJsonResponse(List<String> topPages, int totalMatches) {
        StringBuilder json = new StringBuilder();
        json.append("{\"totalMatches\":").append(totalMatches).append(",\"topPages\":[");
        for (int i = 0; i < topPages.size(); i++) {
            if (i > 0) json.append(",");
            json.append("\"").append(topPages.get(i)).append("\"");
        }
        json.append("]}");
        return json.toString();
    }
}