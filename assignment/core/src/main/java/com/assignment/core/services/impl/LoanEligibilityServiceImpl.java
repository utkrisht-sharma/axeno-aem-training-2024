package com.assignment.core.services.impl;

import com.assignment.core.services.LoanEligibilityService;
import org.osgi.service.component.annotations.Component;

/**
 * This service provides functionality to determine if a client is eligible for a loan
 * based on their income, loan amount, loan term, existing EMIs, and interest rate.
 */
@Component(
        name = "Loan Eligibility Service",
        service = LoanEligibilityService.class,
        immediate = true
)
public class LoanEligibilityServiceImpl implements LoanEligibilityService {

    /**
     * Determines if the client is eligible for the loan or not.
     *
     * @param clientIncome   monthly income of the client.
     * @param loanAmount     amount of the loan.
     * @param loanTerm       term of the loan in months.
     * @param existingEMIs   total existing EMIs that the client is currently paying.
     * @param interestRate   annual interest rate (in percentage) of the loan.
     * @return {@code true} if the client's total EMIs and the new loan EMI, are within
     *         50% of their monthly income; {@code false} otherwise.
     */
    @Override
    public boolean isEligible(double clientIncome, double loanAmount, int loanTerm, double existingEMIs, double interestRate) {
        double monthlyInterestRate = interestRate / 12 / 100;
        double emi = (loanAmount * monthlyInterestRate * Math.pow(1 + monthlyInterestRate, loanTerm)) /
                (Math.pow(1 + monthlyInterestRate, loanTerm) - 1);

        double maxAllowedEMI = clientIncome * 0.5;
        double totalEMIs = emi + existingEMIs;

        return totalEMIs <= maxAllowedEMI;
    }
}
