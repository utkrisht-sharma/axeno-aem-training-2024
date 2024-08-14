package com.assignment.core.services.impl;

import com.assignment.core.services.HomeLoanRequest;
import com.assignment.core.services.LoanEligibilityService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.commons.json.JSONObject;
import org.osgi.service.component.annotations.Component;

/**
 * Implementation of the LoanEligibilityService interface.
 * Provides methods for determining loan eligibility, calculating EMI, and processing loan requests.
 */
@Component(service = LoanEligibilityService.class, immediate = true)
public class LoanEligibilityServiceImpl implements LoanEligibilityService {

    private double monthlyInterestRate;

    /**
     * Determines if a client is eligible for a loan based on their income and existing liabilities.
     *
     * @param userData The home loan request data.
     * @return true if the client is eligible for the loan, false otherwise.
     */
    @Override
    public boolean isEligible(HomeLoanRequest userData) {
        monthlyInterestRate = userData.getInterestRate() / 12 / 100;
        double emi = calculateEMI(userData);
        return (emi + userData.getExistingEMIs()) <= (userData.getClientIncome() * 0.5);
    }

    /**
     * Calculates the Equated Monthly Installment (EMI) for the loan.
     *
     * @param userData The home loan request data.
     * @return The calculated EMI.
     */
    @Override
    public double calculateEMI(HomeLoanRequest userData) {
        return userData.getLoanAmount() * monthlyInterestRate * Math.pow(1 + monthlyInterestRate, userData.getLoanTerm()) /
                (Math.pow(1 + monthlyInterestRate, userData.getLoanTerm()) - 1);
    }

    /**
     * Extracts home loan request data from the HTTP request.
     *
     * @param request The Sling HTTP request containing the loan request parameters.
     * @return A HomeLoanRequest object populated with the request data.
     */
    @Override
    public HomeLoanRequest extractRequestData(SlingHttpServletRequest request) {
        return new HomeLoanRequest(
                request.getParameter("clientName"),
                Double.parseDouble(request.getParameter("clientIncome")),
                Double.parseDouble(request.getParameter("loanAmount")),
                Integer.parseInt(request.getParameter("loanTerm")),
                Double.parseDouble(request.getParameter("existingEMIs")),
                Double.parseDouble(request.getParameter("interestRate"))
        );
    }

    /**
     * Processes the home loan request and returns the result as a JSON object.
     *
     * @param userData The home loan request data.
     * @return A JSON object containing the loan eligibility result and EMI.
     */
    @Override
    public JSONObject processLoanRequest(HomeLoanRequest userData) {
        JSONObject jsonResponse = new JSONObject();

        if (isEligible(userData)) {
            jsonResponse.put("clientName", userData.getClientName());
            jsonResponse.put("eligible", true);
            jsonResponse.put("emi", calculateEMI(userData));
            jsonResponse.put("message", "Loan Approved");
        } else {
            jsonResponse.put("clientName", userData.getClientName());
            jsonResponse.put("eligible", false);
            jsonResponse.put("emi", "N/A");
            jsonResponse.put("message", "Loan Rejected");
        }

        return jsonResponse;
    }
}
