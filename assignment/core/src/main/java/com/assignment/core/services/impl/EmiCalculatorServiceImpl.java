package com.assignment.core.services.impl;

import com.assignment.core.services.EmiCalculatorService;
import org.osgi.service.component.annotations.Component;

/**
 * Implementation of the EmiCalculatorService interface.
 * This service provides methods to calculate EMI
 * and determine loan eligibility based on the calculated EMI, existing EMIs, and client income.
 */
@Component(service = EmiCalculatorService.class)
public class EmiCalculatorServiceImpl implements EmiCalculatorService {

    /**
     * Determines whether the client is eligible for the loan.
     * The eligibility is based on whether the total EMI (including existing EMIs)
     * does not exceed 50% of the client's monthly income.
     *
     * @param emi the calculated EMI for the new loan.
     * @param existingEMIs the total of the client's existing EMIs.
     * @param clientIncome the client's monthly income.
     * @return true if the client is eligible for the loan; false otherwise.
     */
    @Override
    public boolean isEligible(double emi, double existingEMIs, double clientIncome) {
        return emi + existingEMIs <= clientIncome * 0.5;
    }

    /**
     * Calculates the EMI (Equated Monthly Installment) for the loan.
     * The EMI is calculated using the loan amount, monthly interest rate, and loan term.
     *
     * @param loanAmount the principal amount of the loan.
     * @param monthlyInterestRate the monthly interest rate for the loan.
     * @param loanTerm the term of the loan in months.
     * @return the calculated EMI for the loan.
     */
    @Override
    public double calculateEmi(double loanAmount, double monthlyInterestRate, int loanTerm) {
        return (loanAmount * monthlyInterestRate * Math.pow(1 + monthlyInterestRate, loanTerm))
                / (Math.pow(1 + monthlyInterestRate, loanTerm) - 1);
    }
}
