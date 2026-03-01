package com.rayen.loanManagement.controller;

import com.rayen.loanManagement.entity.Loan;
import com.rayen.loanManagement.model.AmortizationRowResponse;
import com.rayen.loanManagement.model.LoanCreateRequest;
import com.rayen.loanManagement.model.LoanResponse;
import com.rayen.loanManagement.service.ILoanService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class LoanController {

    private final ILoanService loanService;

    public LoanController(ILoanService loanService) {
        this.loanService = loanService;
    }

    // ----- Legacy /loan endpoints (unchanged) -----
    @GetMapping("/loan/retrieve-all-loans")
    public List<Loan> retrieveAllLoans() {
        return loanService.getAllLoan();
    }

    @GetMapping("/loan/retrieve-loan/{loan-id}")
    public Loan retrieveLoan(@PathVariable("loan-id") Long loanId) {
        return loanService.getLoanById(loanId);
    }

    @PostMapping("/loan/add-loan")
    public Loan addLoan(@RequestBody Loan loan) {
        return loanService.addLoan(loan);
    }

    @DeleteMapping("/loan/remove-loan/{loan-id}")
    public void removeLoan(@PathVariable("loan-id") Long loanId) {
        loanService.removeLoan(loanId);
    }

    @PutMapping("/loan/modify-loan")
    public Loan modifyLoan(@RequestBody Loan loan) {
        return loanService.modifyLoan(loan);
    }

    @PutMapping("/loan/approve-loan/{loan-id}")
    public Loan approveLoanLegacy(@PathVariable("loan-id") Long loanId) {
        Loan loan = loanService.getLoanById(loanId);
        if (loan != null) {
            loan.approveLoan();
            return loanService.modifyLoan(loan);
        }
        return null;
    }

    @GetMapping("/loan/calculate-interest/{loan-id}")
    public Float calculateInterest(@PathVariable("loan-id") Long loanId) {
        Loan loan = loanService.getLoanById(loanId);
        return loan != null ? loan.calculateInterest() : null;
    }

    // ----- API /api/loans endpoints (amortization) -----
    @PostMapping("/api/loans")
    @ResponseStatus(HttpStatus.CREATED)
    public LoanResponse create(@RequestBody @Valid LoanCreateRequest request) {
        Loan loan = new Loan();
        loan.setUserId(request.userId());
        loan.setAmount(request.amount());
        loan.setInterestRate(request.interestRate());
        loan.setDurationMonths(request.durationMonths());
        loan.setStatus(request.status());
        loan.setDueDate(request.dueDate());
        Loan saved = loanService.addLoan(loan);
        return loanService.toResponse(saved);
    }

    @PatchMapping("/api/loans/{id}/approve")
    public LoanResponse approve(@PathVariable Long id) {
        loanService.approveLoan(id);
        return loanService.toResponse(loanService.getEntity(id));
    }

    @PostMapping("/api/loans/{id}/amortization/generate")
    public List<AmortizationRowResponse> generateAmortization(@PathVariable Long id) {
        return loanService.generateAmortization(id);
    }

    @GetMapping("/api/loans/{id}/amortization")
    public List<AmortizationRowResponse> getAmortization(@PathVariable Long id) {
        return loanService.getAmortization(id);
    }
}
