package com.assignment.core.services.impl;

import com.assignment.core.loanrequest.HomeLoanRequest;
import com.assignment.core.services.LoanEligibilityService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.commons.json.JSONObject;
import org.osgi.service.component.annotations.Component;


/**
 * Implementation of the {LoanEligibilityService} interface.
 */
@Component(service = LoanEligibilityService.class, immediate = true)
public class LoanEligibilityServiceImpl implements LoanEligibilityService {
    double monthlyInterestRate;

    /**
     * Determines if the client is eligible for a home loan.
     *
     * @param userData The {@code HomeLoanRequest} object containing details of the loan request.
     * @return {true} if the combined EMI and existing EMIs are within 50% of the client's income, {false} otherwise.
     */

    @Override
    public boolean isEligible(HomeLoanRequest userData) {
        boolean eligibility;
        monthlyInterestRate = userData.getInterestRate() / 12 / 100;
        double emi = calculateEMI(userData);
        eligibility = (emi + userData.getExistingEMIs()) <= (userData.getClientIncome() * 0.5);
        return eligibility;
    }

    /**
     * Calculates the EMI for the home loan.
     *
     * @param userData The {HomeLoanRequest} object containing details of the loan request.
     * @return The calculated EMI for the home loan.
     */

    @Override
    public double calculateEMI(HomeLoanRequest userData) {
        double emi = userData.getLoanAmount() * monthlyInterestRate * Math.pow(1 + monthlyInterestRate, userData.getLoanTerm()) / (Math.pow(1 + monthlyInterestRate, userData.getLoanTerm()) - 1);
        return emi;
    }

    /**
     * Extracts loan application data from the request.
     *
     * @param request The Sling HTTP request object containing the loan application data.
     * @return A {HomeLoanRequest} object populated with the request data.
     */
    @Override
    public HomeLoanRequest extractRequestData(SlingHttpServletRequest request) {
        HomeLoanRequest userData;
        String clientName = request.getParameter("clientName");
        double clientIncome = Double.parseDouble(request.getParameter("clientIncome"));
        double loanAmount = Double.parseDouble(request.getParameter("loanAmount"));
        int loanTerm = Integer.parseInt(request.getParameter("loanTerm"));
        double existingEMIs = Double.parseDouble(request.getParameter("existingEMIs"));
        double interestRate = Double.parseDouble(request.getParameter("interestRate"));
        userData = new HomeLoanRequest(clientName, clientIncome, loanAmount, loanTerm, existingEMIs, interestRate);
        return userData;
    }

    /**
     * Processes the loan request to determine eligibility and calculate EMI.
     *
     * @param userData {HomeLoanRequest} object containing the loan application data.
     * @return A {JSONObject} containing the loan eligibility status and EMI calculation.
     */
    @Override
    public JSONObject processLoanRequest(HomeLoanRequest userData) {
        JSONObject jsonResponse = new JSONObject();

        if (isEligible(userData)) {
            double emi = calculateEMI(userData);
            jsonResponse.put("clientName", userData.getClientName());
            jsonResponse.put("eligible", true);
            jsonResponse.put("emi", emi);
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
