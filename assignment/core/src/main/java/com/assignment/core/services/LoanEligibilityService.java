package com.assignment.core.services;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.commons.json.JSONObject;

/**
 * Service interface for loan eligibility operations.
 * Provides methods to determine loan eligibility, calculate EMI, and process loan requests.
 */
public interface LoanEligibilityService {

    /**
     * Determines if a client is eligible for a loan based on the provided data.
     *
     * @param userData The home loan request data.
     * @return true if the client is eligible for the loan, false otherwise.
     */
    boolean isEligible(HomeLoanRequest userData);

    /**
     * Calculates the Equated Monthly Installment (EMI) for the loan based on the provided data.
     *
     * @param userData The home loan request data.
     * @return The calculated EMI.
     */
    double calculateEMI(HomeLoanRequest userData);

    /**
     * Extracts home loan request data from the provided Sling HTTP request.
     *
     * @param request The Sling HTTP request containing the loan request parameters.
     * @return A HomeLoanRequest object populated with the request data.
     */
    HomeLoanRequest extractRequestData(SlingHttpServletRequest request);

    /**
     * Processes the home loan request and returns the result as a JSON object.
     *
     * @param userData The home loan request data.
     * @return A JSON object containing the loan eligibility result and EMI.
     */
    JSONObject processLoanRequest(HomeLoanRequest userData);
}
