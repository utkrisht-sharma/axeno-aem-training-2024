package com.assignment.core.services.impl;

import com.assignment.core.constants.LoanConstants;
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
     * {@inheritDoc}
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
     * {@inheritDoc}
     */

    @Override
    public double calculateEMI(HomeLoanRequest userData) {
        double emi = userData.getLoanAmount() * monthlyInterestRate * Math.pow(1 + monthlyInterestRate, userData.getLoanTerm()) / (Math.pow(1 + monthlyInterestRate, userData.getLoanTerm()) - 1);
        return emi;
    }

    /**
     * {@inheritDoc}
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
     * {@inheritDoc}
     */
    @Override
    public JSONObject processLoanRequest(HomeLoanRequest userData) {
        JSONObject jsonResponse = new JSONObject();

        if (isEligible(userData)) {
            double emi = calculateEMI(userData);
            jsonResponse.put(LoanConstants.CLIENT_NAME, userData.getClientName());
            jsonResponse.put(LoanConstants.ELIGIBLE, LoanConstants.ELIGIBLE_TRUE);
            jsonResponse.put(LoanConstants.EMI, emi);
            jsonResponse.put(LoanConstants.MESSAGE, LoanConstants.LOAN_APPROVED);
        } else {
            jsonResponse.put(LoanConstants.CLIENT_NAME, userData.getClientName());
            jsonResponse.put(LoanConstants.ELIGIBLE, LoanConstants.ELIGIBLE_FALSE);
            jsonResponse.put(LoanConstants.EMI, LoanConstants.EMI_NOT_AVAILABLE);
            jsonResponse.put(LoanConstants.MESSAGE, LoanConstants.LOAN_REJECTED);
        }

        return jsonResponse;
    }

}
