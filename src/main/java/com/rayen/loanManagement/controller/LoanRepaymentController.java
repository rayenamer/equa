package com.rayen.loanManagement.controller;

import com.rayen.loanManagement.model.RepaymentRequest;
import com.rayen.loanManagement.model.RepaymentResponse;
import com.rayen.loanManagement.model.RepaymentSummary;
import com.rayen.loanManagement.service.LoanRepaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/repayments")
public class LoanRepaymentController {

    private final LoanRepaymentService loanRepaymentService;

    public LoanRepaymentController(LoanRepaymentService loanRepaymentService) {
        this.loanRepaymentService = loanRepaymentService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RepaymentResponse makePayment(@RequestBody @Valid RepaymentRequest request) {
        return loanRepaymentService.makePayment(request);
    }

    @GetMapping("/loan/{loanId}")
    public List<RepaymentResponse> getRepaymentsByLoan(@PathVariable Long loanId) {
        return loanRepaymentService.getRepaymentsByLoan(loanId);
    }

    @GetMapping("/user/{userId}")
    public List<RepaymentResponse> getRepaymentsByUser(@PathVariable Long userId) {
        return loanRepaymentService.getRepaymentsByUser(userId);
    }

    @GetMapping("/loan/{loanId}/summary")
    public RepaymentSummary getLoanRepaymentSummary(@PathVariable Long loanId) {
        return loanRepaymentService.getLoanRepaymentSummary(loanId);
    }
}
