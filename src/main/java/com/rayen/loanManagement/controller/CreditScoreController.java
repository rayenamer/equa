package com.rayen.loanManagement.controller;

import com.rayen.loanManagement.entity.CreditScore;
import com.rayen.loanManagement.service.ICreditScoreService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/credit-score")
public class CreditScoreController {

    private final ICreditScoreService creditScoreService;

    public CreditScoreController(ICreditScoreService creditScoreService) {
        this.creditScoreService = creditScoreService;
    }

    @GetMapping("/retrieve-all-credit-scores")
    public List<CreditScore> retrieveAllCreditScores() {
        return creditScoreService.getAllCreditScore();
    }

    @GetMapping("/retrieve-credit-score/{credit-score-id}")
    public CreditScore retrieveCreditScore(@PathVariable("credit-score-id") Long creditScoreId) {
        return creditScoreService.getCreditScoreById(creditScoreId);
    }

    @PostMapping("/add-credit-score")
    public CreditScore addCreditScore(@RequestBody CreditScore creditScore) {
        return creditScoreService.addCreditScore(creditScore);
    }

    @DeleteMapping("/remove-credit-score/{credit-score-id}")
    public void removeCreditScore(@PathVariable("credit-score-id") Long creditScoreId) {
        creditScoreService.removeCreditScore(creditScoreId);
    }

    @PutMapping("/modify-credit-score")
    public CreditScore modifyCreditScore(@RequestBody CreditScore creditScore) {
        return creditScoreService.modifyCreditScore(creditScore);
    }

    @PutMapping("/update-score/{credit-score-id}")
    public CreditScore updateScore(@PathVariable("credit-score-id") Long creditScoreId) {
        CreditScore creditScore = creditScoreService.getCreditScoreById(creditScoreId);
        if (creditScore != null) {
            creditScore.updateScore();
            return creditScoreService.modifyCreditScore(creditScore);
        }
        return null;
    }
}
