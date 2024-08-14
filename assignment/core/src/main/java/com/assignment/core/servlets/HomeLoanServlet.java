package com.assignment.core.servlets;

import com.assignment.core.services.HomeLoanRequest;
import com.assignment.core.services.LoanEligibilityService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.commons.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Pattern;

/**
 * Servlet that handles POST requests for home loan eligibility.
 * Processes loan requests, validates input data, and returns the result as a JSON response.
 */
@Component(service = Servlet.class, property = {
        ServletResolverConstants.SLING_SERVLET_METHODS + "=POST",
        ServletResolverConstants.SLING_SERVLET_RESOURCE_TYPES + "=assignment/components/homeloaneligibilityform",
        ServletResolverConstants.SLING_SERVLET_EXTENSIONS + "=json"
})
public class HomeLoanServlet extends SlingAllMethodsServlet {

    @Reference
    private LoanEligibilityService eligibilityService;

    /**
     * Handles POST requests to process a home loan request.
     * Extracts loan request data from the request, validates it, and returns the eligibility result as JSON.
     *
     * @param request  The Sling HTTP request containing the loan request data.
     * @param response The Sling HTTP response to be sent back to the client.
     * @throws ServletException If an error occurs during servlet processing.
     * @throws IOException      If an input or output error occurs.
     */
    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        PrintWriter writer = response.getWriter();
        response.setContentType("application/json");
        HomeLoanRequest loanRequest = eligibilityService.extractRequestData(request);

        JSONObject jsonResponse;
        if (validateInputData(loanRequest)) {
            jsonResponse = eligibilityService.processLoanRequest(loanRequest);
        } else {
            jsonResponse = new JSONObject();
            jsonResponse.put("invalidField", true);
        }
        writer.write(jsonResponse.toString());
    }

    /**
     * Validates the input data for the home loan request.
     * Checks that all fields are within acceptable ranges and that the client name matches the required pattern.
     *
     * @param userData The home loan request data to be validated.
     * @return true if the input data is valid, false otherwise.
     */
    private boolean validateInputData(HomeLoanRequest userData) {
        String NAME_REGEX = "^[a-zA-Z\\s]{3,30}$";
        if (!Pattern.compile(NAME_REGEX).matcher(userData.getClientName()).matches()) {
            return false;
        }
        return userData.getClientIncome() >= 0 &&
                userData.getLoanAmount() >= 0 &&
                userData.getLoanTerm() >= 0 &&
                userData.getInterestRate() >= 0 &&
                userData.getExistingEMIs() >= 0;
    }
}
