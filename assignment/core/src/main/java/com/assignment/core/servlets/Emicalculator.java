package com.assignment.core.servlets;

import com.assignment.core.services.EmiCalculatorService;
import com.assignment.core.services.ParameterValidationService;
import com.google.gson.Gson;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * This is a Servlet that handles EMI calculation
 * This servlet listens to POST requests and processes home loan applications fior calculating emi
 */
@Component(
        service = Servlet.class,
        property = {
                "sling.servlet.resourceTypes=assignment/components/emicalculator",
                "sling.servlet.methods=POST",
                "sling.servlet.extensions=json"
        }
)
public class Emicalculator extends SlingAllMethodsServlet {

    private static final Logger LOG = LoggerFactory.getLogger(Emicalculator.class);

    @Reference
    private EmiCalculatorService emiCalculatorService;

    @Reference
    private ParameterValidationService parameterValidationService;

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        Gson gson = new Gson();
        Map<String, Object> responseMap = new HashMap<>();

        try {
            // Read and validate parameters from the request
            String clientName = request.getParameter("clientName");
            String clientIncomeStr = request.getParameter("clientIncome");
            String loanAmountStr = request.getParameter("loanAmount");
            String loanTermStr = request.getParameter("loanTerm");
            String existingEMIsStr = request.getParameter("existingEMIs");
            String interestRateStr = request.getParameter("interestRate");

            if (!parameterValidationService.validateString("clientName", clientName) ||
                    !parameterValidationService.validateDouble("clientIncome", clientIncomeStr) ||
                    !parameterValidationService.validateDouble("loanAmount", loanAmountStr) ||
                    !parameterValidationService.validateInteger("loanTerm", loanTermStr) ||
                    !parameterValidationService.validateDouble("existingEMIs", existingEMIsStr) ||
                    !parameterValidationService.validateDouble("interestRate", interestRateStr)) {

                response.setStatus(SlingHttpServletResponse.SC_BAD_REQUEST);
                responseMap.put("error", "Invalid request parameters.");
                response.setContentType("application/json");
                response.getWriter().write(gson.toJson(responseMap));
                return;
            }

            Double clientIncome = Double.parseDouble(clientIncomeStr);
            Double loanAmount = Double.parseDouble(loanAmountStr);
            Integer loanTerm = Integer.parseInt(loanTermStr);
            Double existingEMIs = Double.parseDouble(existingEMIsStr);
            Double interestRate = Double.parseDouble(interestRateStr);

            // Calculate the EMI using the service
            double monthlyInterestRate = interestRate / 12 / 100;
            double emi = emiCalculatorService.calculateEmi(loanAmount, monthlyInterestRate, loanTerm);

            // Determine loan eligibility
            boolean isEligible = emiCalculatorService.isEligible(emi, existingEMIs, clientIncome);

            // Here Preparing the response map
            responseMap.put("clientName", clientName);
            responseMap.put("eligible", isEligible);
            responseMap.put("emi", isEligible ? String.format("%.2f", emi) : "N/A");
            responseMap.put("message", isEligible ? "Loan Approved" : "Loan Rejected");

            // Setting the response content type
            response.setContentType("application/json");
            response.getWriter().write(gson.toJson(responseMap));
        } catch (Exception e) {
            // Log the error and set the response status to 500
            LOG.error("Error processing loan application", e);
            response.setStatus(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            responseMap.put("error", "An error occurred while processing the request.");
            response.setContentType("application/json");
            response.getWriter().write(gson.toJson(responseMap));
        }
    }
}
