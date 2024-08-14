package com.assignment.core.servlets;


import com.assignment.core.loanrequest.HomeLoanRequest;
import com.assignment.core.services.LoanEligibilityService;
import com.fasterxml.jackson.databind.util.JSONPObject;
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
import java.util.regex.Matcher;

/**
 * Servlet for processing home loan application.
 * This servlet handles HTTP POST requests for home loan processing.
 */
@Component(service = Servlet.class, property = {ServletResolverConstants.SLING_SERVLET_METHODS + "=POST", ServletResolverConstants.SLING_SERVLET_RESOURCE_TYPES + "=assignment/components/homeloaneligibilityform", ServletResolverConstants.SLING_SERVLET_EXTENSIONS + "=json"})
public class HomeLoanServlet extends SlingAllMethodsServlet {

    @Reference
    LoanEligibilityService eligibilityService;

    /**
     * Handles POST requests for home loan processing.
     *
     * @param request  The Sling HTTP request object containing the loan application data.
     * @param response The Sling HTTP response object used to send the JSON response.
     * @throws ServletException If an error occurs during servlet processing.
     * @throws IOException      If an I/O error occurs while writing the response.
     */

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        PrintWriter writer = response.getWriter();
        response.setContentType("application/json");
        HomeLoanRequest loanRequest = eligibilityService.extractRequestData(request);
        if (validateInputData(loanRequest)) {
            JSONObject jsonResponse = eligibilityService.processLoanRequest(loanRequest);
            writer.write(jsonResponse.toString());

        } else {
            JSONObject validationResponse = new JSONObject();
            validationResponse.put("invalidField", true);
            writer.write(validationResponse.toString());
        }


    }


    private boolean validateInputData(HomeLoanRequest userData) {
        String NAME_REGEX = "^[a-zA-Z\\s]{3,30}$";
        Pattern namePattern = Pattern.compile(NAME_REGEX);
        Matcher nameMatcher = namePattern.matcher(userData.getClientName());
        if (!nameMatcher.matches()) {
            return false;
        }

        if ((userData.getClientIncome() < 0)) {
            return false;
        }

        if ((userData.getLoanAmount()) < 0) {
            return false;
        }
        if ((userData.getLoanTerm()) < 0) {
            return false;
        }
        if (userData.getInterestRate() < 0) {
            return false;
        }
        if ((userData.getExistingEMIs()) < 0) {
            return false;
        }

        return true;

    }


}


