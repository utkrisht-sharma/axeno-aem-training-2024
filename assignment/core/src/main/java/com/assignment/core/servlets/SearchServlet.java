package com.assignment.core.servlets;

import com.assignment.core.services.NodeManagementService;
import com.assignment.core.services.SearchService;
import com.assignment.core.services.ValidationService;
import com.day.cq.wcm.api.WCMException;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.ServletResolverConstants;
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
                ServletResolverConstants.SLING_SERVLET_PATHS + "=/bin/search",
                ServletResolverConstants.SLING_SERVLET_METHODS + "=POST",

                "sling.servlet.methods=POST",
                "sling.servlet.paths=/bin/search"
        }
)
public class SearchServlet extends SlingAllMethodsServlet {

    private static final Logger LOG = LoggerFactory.getLogger(SearchServlet.class);

    @Reference
    private SearchService searchService;

    @Reference
    private ValidationService validationService;

    @Reference
    private NodeManagementService nodeManagementService;

    @Reference
    private ResourceResolverFactory resourceResolverFactory;
    /**
     * The parameter name for the path in the request.
     */
    private static final String PARAM_PATH = "path";

    /**
     * The parameter name for the first property in the request.
     */
    private static final String PARAM_PROPERTY_ONE = "propertyOne";

    /**
     * The parameter name for the value of the first property in the request.
     */
    private static final String PARAM_PROPERTY_ONE_VALUE = "propertyOneValue";

    /**
     * The parameter name for the second property in the request.
     */
    private static final String PARAM_PROPERTY_TWO = "propertyTwo";

    /**
     * The parameter name for the value of the second property in the request.
     */
    private static final String PARAM_PROPERTY_TWO_VALUE = "propertyTwoValue";

    /**
     * The parameter name for the save flag in the request.
     */
    private static final String PARAM_SAVE = "save";

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

        String path = request.getParameter(PARAM_PATH);
        String propertyOne = request.getParameter(PARAM_PROPERTY_ONE);
        String propertyOneValue = request.getParameter(PARAM_PROPERTY_ONE_VALUE);
        String propertyTwo = request.getParameter(PARAM_PROPERTY_TWO);
        String propertyTwoValue = request.getParameter(PARAM_PROPERTY_TWO_VALUE);
        boolean save = Boolean.parseBoolean(request.getParameter(PARAM_SAVE));

        String validationError = validationService.validateParameters(path, propertyOne, propertyOneValue, propertyTwo, propertyTwoValue);
        if (validationError != null) {
            response.setStatus(SlingHttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(validationError);
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
            LOG.error(String.format("Error in SearchServlet: %s", e.getMessage()), e);

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
