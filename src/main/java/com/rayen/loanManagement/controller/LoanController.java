package com.rayen.loanManagement.controller;

import com.rayen.loanManagement.entity.Loan;
import com.rayen.loanManagement.service.ILoanService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/loan")
public class LoanController {

    private final ILoanService loanService;

    public LoanController(ILoanService loanService) {

        this.loanService = loanService;
    }

    @GetMapping("/retrieve-all-loans")
    public List<Loan> retrieveAllLoans() {
        return loanService.getAllLoan();
    }

    @GetMapping("/retrieve-loan/{loan-id}")
    public Loan retrieveLoan(@PathVariable("loan-id") Long loanId) {
        return loanService.getLoanById(loanId);
    }

    @PostMapping("/add-loan")
    public Loan addLoan(@RequestBody Loan loan) {
        return loanService.addLoan(loan);
    }

    @DeleteMapping("/remove-loan/{loan-id}")
    public void removeLoan(@PathVariable("loan-id") Long loanId) {
        loanService.removeLoan(loanId);
    }

    @PutMapping("/modify-loan")
    public Loan modifyLoan(@RequestBody Loan loan) {
        return loanService.modifyLoan(loan);
    }

    @PutMapping("/approve-loan/{loan-id}")
    public Loan approveLoan(@PathVariable("loan-id") Long loanId) {
        Loan loan = loanService.getLoanById(loanId);
        if (loan != null) {
            loan.approveLoan();
            return loanService.modifyLoan(loan);
        }
        return null;
    }

    @GetMapping("/calculate-interest/{loan-id}")
    public Float calculateInterest(@PathVariable("loan-id") Long loanId) {
        Loan loan = loanService.getLoanById(loanId);
        return loan != null ? loan.calculateInterest() : null;
    }
}
