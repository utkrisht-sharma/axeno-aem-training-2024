package com.assignment.core.servlets;

import com.assignment.core.services.NodeService;
import com.assignment.core.services.SearchService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.List;

/**
 * Servlet for handling page search requests and processing the results.
 * It performs a search based on parameters, handles saving or deleting nodes,
 * and responds with the search results in JSON format.
 */
@Component(service = Servlet.class,
        property = {
                ServletResolverConstants.SLING_SERVLET_PATHS + "=/bin/pagesearch",
                ServletResolverConstants.SLING_SERVLET_METHODS + "=POST"
        })
public class PageSearchServlet extends SlingAllMethodsServlet {

    private static final Logger logger = LoggerFactory.getLogger(PageSearchServlet.class);

    @Reference
    private SearchService searchService;

    @Reference
    private NodeService nodeService;

    /**
     * Handles POST requests to search for pages and process results.
     *
     * @param request the {@link SlingHttpServletRequest} containing the search parameters and request data.
     * @param response the {@link SlingHttpServletResponse} used to send the response back to the client.
     * @throws IOException if an I/O error occurs while processing the request or sending the response.
     * @throws ServletException if the request cannot be processed due to a servlet-related issue.
     */
    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException, ServletException {
        String searchPath = request.getParameter("path");
        String propOne = request.getParameter("propertyOne");
        String propOneVal = request.getParameter("propertyOneValue");
        String propTwo = request.getParameter("propertyTwo");
        String propTwoVal = request.getParameter("propertyTwoValue");
        String saveFlag = request.getParameter("save");

        if (StringUtils.isAnyBlank(searchPath, propOne, propOneVal, propTwo, propTwoVal, saveFlag)) {
            response.sendError(SlingHttpServletResponse.SC_BAD_REQUEST, "Required parameters are missing.");
            return;
        }

        try (ResourceResolver resolver = request.getResourceResolver()) {
            // Step 1: Perform page search using the service
            Pair<List<String>, Long> searchResults = searchService.executePageSearch(resolver, searchPath, propOne, propOneVal, propTwo, propTwoVal);
            List<String> foundPaths = searchResults.getLeft();
            long matchCount = searchResults.getRight();

            // Step 2: Save or delete nodes using the service
            nodeService.processSaveOrDelete(resolver, saveFlag, matchCount);

            // Respond with the results
            response.setContentType("application/json");
            response.getWriter().write("{\"topResults\":" + foundPaths + ", \"totalMatches\":" + matchCount + "}");
        } catch (Exception e) {
            logger.error("Request processing failed", e);
            response.sendError(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
