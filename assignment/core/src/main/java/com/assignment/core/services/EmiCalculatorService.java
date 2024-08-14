package com.assignment.core.services;

/**
 * Service interface for calculating EMI (Equated Monthly Installment)
 * and determining loan eligibility based on the EMI and client's income.
 */
public interface EmiCalculatorService {

    /**
     * Determines whether the client is eligible for the loan based on their EMI,
     * existing EMIs, and income.
     *
     * @param emi the calculated EMI for the new loan.
     * @param existingEMIs the total of the client's existing EMIs.
     * @param clientIncome the client's monthly income.
     * @return true if the client is eligible for the loan (i.e., the total EMI does not
     *         exceed 50% of the client's income); false otherwise.
     */
    boolean isEligible(double emi, double existingEMIs, double clientIncome);

    /**
     * Calculates the EMI (Equated Monthly Installment) for the loan based on
     * the principal amount, monthly interest rate, and loan term.
     *
     * @param loanAmount the principal amount of the loan.
     * @param monthlyInterestRate the monthly interest rate for the loan (calculated
     *                            from the annual interest rate).
     * @param loanTerm the term of the loan in months.
     * @return the calculated EMI for the loan.
     */
    double calculateEmi(double loanAmount, double monthlyInterestRate, int loanTerm);
}
