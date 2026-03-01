package com.rayen.loanManagement.controller;

import com.rayen.loanManagement.model.LoanDecisionRequest;
import com.rayen.loanManagement.model.LoanDecisionResponse;
import com.rayen.loanManagement.service.LoanDecisionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/loans/decision")
public class LoanDecisionController {

    private final LoanDecisionService loanDecisionService;

    public LoanDecisionController(LoanDecisionService loanDecisionService) {
        this.loanDecisionService = loanDecisionService;
    }

    @PostMapping("/evaluate")
    public LoanDecisionResponse evaluate(@RequestBody @Valid LoanDecisionRequest request) {
        return loanDecisionService.evaluate(request);
    }

    @GetMapping("/simulate")
    public LoanDecisionResponse simulate(
            @RequestParam BigDecimal amount,
            @RequestParam BigDecimal rate,
            @RequestParam Integer months,
            @RequestParam BigDecimal income,
            @RequestParam(required = false) Long userId) {
        return loanDecisionService.simulate(amount, rate, months, income, userId);
    }
}
