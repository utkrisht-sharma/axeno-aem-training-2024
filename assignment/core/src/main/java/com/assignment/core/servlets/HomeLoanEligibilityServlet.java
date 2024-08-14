package com.assignment.core.servlets;

import com.assignment.core.services.EMICalculationService;
import com.assignment.core.services.LoanEligibilityService;
import com.assignment.core.services.ValidationService;
import com.google.gson.JsonObject;
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

@Component(
        service = { Servlet.class },
        property = {
                "sling.servlet.resourceTypes=assignment/components/homeloancalculator",
                "sling.servlet.methods=POST",
                "sling.servlet.extensions=json"
        }
)
public class HomeLoanEligibilityServlet extends SlingAllMethodsServlet {

    private static final Logger LOG = LoggerFactory.getLogger(HomeLoanEligibilityServlet.class);

    @Reference
    private LoanEligibilityService loanEligibilityService;

    @Reference
    private ValidationService validationService;

    @Reference
    private EMICalculationService emiCalculationService;

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        JsonObject jsonResponse = new JsonObject();

        try {
            // Fetch parameters from the request
            String clientName = request.getParameter("clientName");
            String clientIncome = request.getParameter("clientIncome");
            String loanAmount = request.getParameter("loanAmount");
            String loanTerm = request.getParameter("loanTerm");
            String existingEMIs = request.getParameter("existingEMIs");
            String interestRate = request.getParameter("interestRate");

            // Validate parameters
            String errorMessage = validateParameters(clientName, clientIncome, loanAmount, loanTerm, existingEMIs, interestRate);
            if (errorMessage != null) {
                LOG.error("Validation failed: {}", errorMessage);
                response.setStatus(SlingHttpServletResponse.SC_BAD_REQUEST);
                jsonResponse.addProperty("error", errorMessage);
                response.getWriter().write(jsonResponse.toString());
                return;
            }

            // Convert parameters to appropriate types
            double clientIncomeValue = Double.parseDouble(clientIncome);
            double loanAmountValue = Double.parseDouble(loanAmount);
            int loanTermValue = Integer.parseInt(loanTerm);
            double existingEMIsValue = Double.parseDouble(existingEMIs);
            double interestRateValue = Double.parseDouble(interestRate);

            // Check loan eligibility
            boolean isEligible = loanEligibilityService.isEligible(clientIncomeValue, loanAmountValue, loanTermValue, existingEMIsValue, interestRateValue);

            if (isEligible) {
                // Calculate EMI
                double emi = emiCalculationService.calculateEMI(loanAmountValue, interestRateValue, loanTermValue);
                jsonResponse.addProperty("clientName", clientName);
                jsonResponse.addProperty("eligible", true);
                jsonResponse.addProperty("emi", Math.round(emi * 100.0) / 100.0);
                jsonResponse.addProperty("message", "Loan Approved");
            } else {
                jsonResponse.addProperty("clientName", clientName);
                jsonResponse.addProperty("eligible", false);
                jsonResponse.addProperty("emi", "N/A");
                jsonResponse.addProperty("message", "Loan Rejected");
            }

            response.getWriter().write(jsonResponse.toString());

        } catch (Exception e) {
            LOG.error("Error processing home loan eligibility", e);
            response.setStatus(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonResponse.addProperty("error", "An error occurred while processing the request.");
            response.getWriter().write(jsonResponse.toString());
        }
    }

    private String validateParameters(String clientName, String clientIncome, String loanAmount, String loanTerm, String existingEMIs, String interestRate) {
        String errorMessage;

        if ((errorMessage = validationService.validateClientName(clientName)) != null) return errorMessage;
        if ((errorMessage = validationService.validateClientIncome(clientIncome)) != null) return errorMessage;
        if ((errorMessage = validationService.validateLoanAmount(loanAmount)) != null) return errorMessage;
        if ((errorMessage = validationService.validateLoanTerm(loanTerm)) != null) return errorMessage;
        if ((errorMessage = validationService.validateExistingEMIs(existingEMIs)) != null) return errorMessage;
        if ((errorMessage = validationService.validateInterestRate(interestRate)) != null) return errorMessage;
        return null;
    }
}
