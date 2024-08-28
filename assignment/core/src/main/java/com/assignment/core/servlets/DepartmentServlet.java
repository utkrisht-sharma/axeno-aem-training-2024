package com.assignment.core.servlets;

import com.assignment.core.service.DepartmentService;
import com.google.gson.Gson;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.sling.api.servlets.HttpConstants;

/**
 * Servlet for handling HTTP POST requests to retrieve faculty names based on a department name.
 * This servlet listens at the path "/bin/department/faculty" and uses the DepartmentService
 * to fetch faculty names for a given department.
 */
@Component(
        service = { Servlet.class },
        property = {
                "sling.servlet.paths=/bin/department/faculty",
                ServletResolverConstants.SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_POST
        }
)
public class DepartmentServlet extends SlingAllMethodsServlet {

    private static final Logger LOG = LoggerFactory.getLogger(DepartmentServlet.class);

    /**
     * Reference to the DepartmentService for fetching faculty names.
     */
    @Reference
    private DepartmentService departmentService;

    /**
     * Handles HTTP POST requests to retrieve faculty names for a given department.
     * Expects a "department" parameter in the request to specify the department name.
     *
     * @param request  the SlingHttpServletRequest object containing client request data
     * @param response the SlingHttpServletResponse object to send response data
     * @throws IOException if an input or output error is detected when the servlet handles the request
     */
    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        // Extract department name parameter from the request
        String departmentName = request.getParameter("department");

        // Check if department name is provided
        if (departmentName == null || departmentName.trim().isEmpty()) {
            LOG.warn("Department name parameter is missing or empty.");
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"error\": \"Department name is required\"}");
            return;
        }

        // Fetch faculty names using the DepartmentService
        List<String> facultyNames = departmentService.getFacultyNames(departmentName);

        // Prepare response
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        if (facultyNames != null && !facultyNames.isEmpty()) {
            // Eliminate duplicates and send the response
            facultyNames = facultyNames.stream().distinct().collect(Collectors.toList());
            response.getWriter().write(new Gson().toJson(facultyNames));
            LOG.info("Faculty names for department '{}': {}", departmentName, facultyNames);
        } else {
            // Department does not exist
            response.getWriter().write("{\"error\": \"Department does not exist\"}");
            LOG.info("No faculty found for department '{}'", departmentName);
        }
    }
}
