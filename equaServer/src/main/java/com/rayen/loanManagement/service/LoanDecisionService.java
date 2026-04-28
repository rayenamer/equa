package com.rayen.loanManagement.service;

import com.rayen.loanManagement.entity.CreditScore;
import com.rayen.loanManagement.entity.Loan;
import com.rayen.loanManagement.model.LoanDecisionRequest;
import com.rayen.loanManagement.model.LoanDecisionResponse;
import com.rayen.loanManagement.repository.CreditScoreRepository;
import com.rayen.loanManagement.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanDecisionService {

    private static final int SCALE = 2;
    private static final RoundingMode ROUNDING = RoundingMode.HALF_EVEN;
    private static final String APPROVED = "APPROVED";
    private static final String MANUAL_REVIEW = "MANUAL_REVIEW";
    private static final String REJECTED = "REJECTED";

    private final LoanRepository loanRepository;
    private final CreditScoreRepository creditScoreRepository;
    private final ILoanService loanService;

    public LoanDecisionResponse evaluate(LoanDecisionRequest req) {
        Loan loan = loanRepository.findById(req.loanId())
            .orElseThrow(() -> new IllegalArgumentException("Loan not found"));
        CreditScore creditScore = creditScoreRepository.findByUserId(req.userId())
            .orElseGet(() -> {
                CreditScore cs = new CreditScore();
                cs.setUserId(req.userId());
                cs.setScore(500);
                cs.setLatePayments(0);
                return cs;
            });

        BigDecimal monthlyPayment = loan.getMonthlyPayment();
        if (monthlyPayment == null && loan.getDurationMonths() != null) {
            monthlyPayment = loanService.computeMonthlyPayment(
                loan.getAmount(), loan.getInterestRate(), loan.getDurationMonths());
        }

        return buildDecision(
            loan.getLoanId(),
            req.userId(),
            creditScore.getScore() != null ? creditScore.getScore() : 500,
            creditScore.getLatePayments() != null ? creditScore.getLatePayments() : 0,
            monthlyPayment != null ? monthlyPayment : BigDecimal.ZERO,
            req.monthlyIncome(),
            loan.getAmount(),
            true,
            loan
        );
    }

    public LoanDecisionResponse simulate(BigDecimal amount, BigDecimal rate, Integer months, BigDecimal income, Long userId) {
        if (amount == null || rate == null || months == null || income == null || income.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("amount, rate, months, and income are required for simulation");
        }
        BigDecimal monthlyPayment = loanService.computeMonthlyPayment(amount.floatValue(), rate.floatValue(), months);
        CreditScore creditScore = userId != null ? creditScoreRepository.findByUserId(userId).orElse(null) : null;
        int score = creditScore != null && creditScore.getScore() != null ? creditScore.getScore() : 500;
        int latePayments = creditScore != null && creditScore.getLatePayments() != null ? creditScore.getLatePayments() : 0;

        return buildDecision(
            null,
            userId,
            score,
            latePayments,
            monthlyPayment,
            income,
            amount.doubleValue(),
            false,
            null
        );
    }

    private LoanDecisionResponse buildDecision(
            Long loanId,
            Long userId,
            int creditScoreValue,
            int latePayments,
            BigDecimal monthlyPayment,
            BigDecimal monthlyIncome,
            double loanAmount,
            boolean applyIfApproved,
            Loan loan
    ) {
        int creditScorePoints = 0;
        if (creditScoreValue >= 700) creditScorePoints = 40;
        else if (creditScoreValue >= 600) creditScorePoints = 30;
        else if (creditScoreValue >= 500) creditScorePoints = 20;
        else if (creditScoreValue >= 400) creditScorePoints = 10;

        BigDecimal debtRatio = BigDecimal.ZERO;
        int debtRatioPoints = 0;
        if (monthlyIncome.compareTo(BigDecimal.ZERO) > 0 && monthlyPayment != null) {
            debtRatio = monthlyPayment.multiply(BigDecimal.valueOf(100)).divide(monthlyIncome, SCALE, ROUNDING);
            double dr = debtRatio.doubleValue();
            if (dr <= 20) debtRatioPoints = 30;
            else if (dr <= 30) debtRatioPoints = 20;
            else if (dr <= 40) debtRatioPoints = 10;
        }

        BigDecimal annualIncome = monthlyIncome.multiply(BigDecimal.valueOf(12));
        double amountRatio = annualIncome.compareTo(BigDecimal.ZERO) > 0
            ? loanAmount / annualIncome.doubleValue() : 0;
        int amountRatioPoints = 0;
        if (amountRatio <= 2) amountRatioPoints = 20;
        else if (amountRatio <= 4) amountRatioPoints = 10;
        else if (amountRatio <= 6) amountRatioPoints = 5;

        int latePaymentPoints = 0;
        if (latePayments == 0) latePaymentPoints = 10;
        else if (latePayments == 1) latePaymentPoints = 5;
        else if (latePayments <= 3) latePaymentPoints = 2;

        int totalScore = creditScorePoints + debtRatioPoints + amountRatioPoints + latePaymentPoints;
        String decision;
        if (totalScore >= 70) decision = APPROVED;
        else if (totalScore >= 50) decision = MANUAL_REVIEW;
        else decision = REJECTED;

        if (applyIfApproved && APPROVED.equals(decision) && loan != null) {
            loanService.approveLoan(loan.getLoanId());
        }

        String reason = String.format(
            "Score: %d/100. Credit: %dpts, Debt ratio: %s%%, Amount ratio: %.2f, Late: %dpts",
            totalScore, creditScorePoints, debtRatio.toString(), amountRatio, latePaymentPoints
        );

        List<String> tips = new ArrayList<>();
        if (creditScorePoints < 40) tips.add("Improve credit score (e.g. pay on time, reduce debt)");
        if (debtRatioPoints < 30) tips.add("Lower debt-to-income ratio (increase income or reduce loan amount)");
        if (amountRatioPoints < 20) tips.add("Reduce loan amount relative to income");
        if (latePaymentPoints < 10) tips.add("Avoid late payments to gain more points");
        String recommendation = tips.isEmpty() ? "Application meets criteria." : String.join(" ", tips);

        return new LoanDecisionResponse(
            loanId,
            userId,
            decision,
            totalScore,
            creditScorePoints,
            debtRatioPoints,
            amountRatioPoints,
            latePaymentPoints,
            monthlyPayment,
            debtRatio,
            reason,
            recommendation
        );
    }
}
