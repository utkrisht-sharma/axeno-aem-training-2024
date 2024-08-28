package com.assignment.core.servlets;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.commons.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;

/**
 * A Sling servlet that calculates the total marks from the provided marks parameter.
 */
@Component(service = Servlet.class, property = {ServletResolverConstants.SLING_SERVLET_PATHS + "=/bin/calculateTotalMarks", ServletResolverConstants.SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_POST, ServletResolverConstants.SLING_SERVLET_EXTENSIONS + "=json"})
public class CalculateMarksServlet extends SlingAllMethodsServlet {

    private static final Logger LOG = LoggerFactory.getLogger(CalculateMarksServlet.class);

    /**
     * Handles POST requests to calculate the total marks.
     */
    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        String marksParam = request.getParameter("marks");
        float totalMarks = 0;
        if (marksParam != null && !marksParam.isEmpty()) {

            String[] marksArray = marksParam.split(",");
            for (String mark : marksArray) {
                totalMarks += Float.parseFloat(mark.trim());
            }

            LOG.info("Total marks calculated: {}", totalMarks);

        } else {
            LOG.warn("No marks parameter received in the request.");
            response.setStatus(SlingHttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("totalMarks", totalMarks);
        response.setContentType("application/json");
        response.getWriter().write(jsonResponse.toString());
    }
}
