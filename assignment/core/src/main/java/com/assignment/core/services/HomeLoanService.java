package com.assignment.core.services;

import org.osgi.service.component.annotations.Component;

@Component(service = HomeLoanService.class,immediate = true)
public interface HomeLoanService {
    /**
     * Checks if the client is eligible for a loan based on their income,
     * existing EMIs, and the new loan parameters.
     * <p>
     * Eligibility is determined by ensuring that the total EMI (including
     * existing EMIs) does not exceed 50% of the client's income.
     * </p>
     *
     * @param clientIncome The client's monthly income.
     * @param existingEMIs The total of existing EMIs.
     * @param loanAmount   The amount of the new loan.
     * @param loanTerm     The tenure of the new loan in months.
     * @param interestRate The annual interest rate for the new loan.
     * @return true if the client is eligible for the loan, false otherwise.
     */
    boolean isEligible(Double clientIncome, Double existingEMIs, Double loanAmount, int loanTerm, Double interestRate);
    /**
     * Calculates the EMI (Equated Monthly Installment) based on the loan
     * amount, loan term, and annual interest rate.
     * <p>
     * The EMI is computed using the formula:
     * EMI = [P x R x (1+R)^N] / [(1+R)^N - 1]
     * where P is the loan amount, R is the monthly interest rate, and N is
     * the loan term in months.
     * </p>
     *
     * @param loanAmount   The principal loan amount.
     * @param loanTerm     The tenure of the loan in months.
     * @param interestRate The annual interest rate for the loan.
     * @return The calculated EMI as a double.
     */
    Double calculateEMI(Double loanAmount, int loanTerm, Double interestRate);
}
