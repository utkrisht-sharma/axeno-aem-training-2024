package com.assignment.core.services.impl;

import com.assignment.core.services.LoanEligibilityService;
import org.osgi.service.component.annotations.Component;

@Component(
        service = LoanEligibilityService.class
)
public class LoanEligibilityServiceImpl implements LoanEligibilityService {

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
