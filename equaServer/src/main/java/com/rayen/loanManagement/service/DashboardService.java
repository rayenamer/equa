package com.rayen.loanManagement.service;

import com.rayen.loanManagement.entity.CreditScore;
import com.rayen.loanManagement.entity.Loan;
import com.rayen.loanManagement.entity.LoanRepayment;
import com.rayen.loanManagement.entity.MicroInsurance;
import com.rayen.loanManagement.model.*;
import com.rayen.loanManagement.repository.CreditScoreRepository;
import com.rayen.loanManagement.repository.LoanRepaymentRepository;
import com.rayen.loanManagement.repository.LoanRepository;
import com.rayen.loanManagement.repository.MicroInsuranceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private static final int SCALE = 2;
    private static final RoundingMode ROUNDING = RoundingMode.HALF_EVEN;
    private static final String APPROVED = "APPROVED";
    private static final String PENDING = "PENDING";
    private static final String DEFAULTED = "DEFAULTED";
    private static final String COMPLETED = "COMPLETED";
    private static final String ON_TIME = "ON_TIME";
    private static final String LATE = "LATE";
    private static final String MISSED = "MISSED";
    private static final String ACTIVE = "ACTIVE";
    private static final String CANCELLED = "CANCELLED";

    private final LoanRepository loanRepository;
    private final LoanRepaymentRepository repaymentRepository;
    private final CreditScoreRepository creditScoreRepository;
    private final MicroInsuranceRepository microInsuranceRepository;
    private final ILoanService loanService;
    private final IMicroInsuranceService microInsuranceService;

    public DashboardResponse getGlobalDashboard() {
        List<Loan> allLoans = loanRepository.findAll();
        long totalLoans = allLoans.size();
        long approvedLoans = allLoans.stream().filter(l -> APPROVED.equals(l.getStatus())).count();
        long pendingLoans = allLoans.stream().filter(l -> PENDING.equals(l.getStatus())).count();
        long defaultedLoans = allLoans.stream().filter(l -> DEFAULTED.equals(l.getStatus())).count();
        long completedLoans = allLoans.stream().filter(l -> COMPLETED.equals(l.getStatus())).count();

        BigDecimal totalLoanAmount = allLoans.stream()
            .map(l -> BigDecimal.valueOf(l.getAmount()))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal averageLoanAmount = totalLoans > 0
            ? totalLoanAmount.divide(BigDecimal.valueOf(totalLoans), SCALE, ROUNDING)
            : BigDecimal.ZERO;

        BigDecimal totalInterestProjected = allLoans.stream()
            .filter(l -> l.getMonthlyPayment() != null && l.getDurationMonths() != null)
            .map(l -> l.getMonthlyPayment().multiply(BigDecimal.valueOf(l.getDurationMonths())).subtract(BigDecimal.valueOf(l.getAmount())))
            .reduce(BigDecimal.ZERO, BigDecimal::add).max(BigDecimal.ZERO);

        long totalRepayments = repaymentRepository.count();
        long onTimeRepayments = repaymentRepository.countByPaymentStatus(ON_TIME);
        long lateRepayments = repaymentRepository.countByPaymentStatus(LATE);
        long missedRepayments = repaymentRepository.countByPaymentStatus(MISSED);

        List<LoanRepayment> allRepayments = repaymentRepository.findAll();
        BigDecimal totalAmountCollected = allRepayments.stream()
            .map(LoanRepayment::getAmountPaid)
            .filter(java.util.Objects::nonNull)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalPenaltiesCollected = allRepayments.stream()
            .map(LoanRepayment::getPenaltyAmount)
            .filter(java.util.Objects::nonNull)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        double onTimePaymentRate = totalRepayments > 0 ? (100.0 * onTimeRepayments / totalRepayments) : 0.0;

        List<CreditScore> allScores = creditScoreRepository.findAll();
        long totalUsers = allScores.size();
        double averageCreditScore = totalUsers > 0
            ? allScores.stream().mapToInt(cs -> cs.getScore() != null ? cs.getScore() : 0).average().orElse(0)
            : 0;
        long lowRiskUsers = allScores.stream().filter(cs -> cs.getScore() != null && cs.getScore() >= 700).count();
        long mediumRiskUsers = allScores.stream().filter(cs -> cs.getScore() != null && cs.getScore() >= 500 && cs.getScore() < 700).count();
        long highRiskUsers = allScores.stream().filter(cs -> cs.getScore() != null && cs.getScore() < 500).count();

        List<MicroInsurance> allInsurances = microInsuranceRepository.findAll();
        long totalInsurances = allInsurances.size();
        long activeInsurances = allInsurances.stream().filter(i -> ACTIVE.equals(i.getStatus())).count();
        long cancelledInsurances = allInsurances.stream().filter(i -> CANCELLED.equals(i.getStatus())).count();
        BigDecimal totalPremiumsCollected = allInsurances.stream()
            .map(MicroInsurance::getPremium)
            .filter(java.util.Objects::nonNull)
            .map(BigDecimal::valueOf)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        Map<String, Long> insuranceByType = allInsurances.stream()
            .collect(Collectors.groupingBy(i -> i.getType() != null ? i.getType() : "UNKNOWN", Collectors.counting()));

        BigDecimal defaultRate = totalLoans > 0
            ? BigDecimal.valueOf(100.0 * defaultedLoans / totalLoans).setScale(SCALE, ROUNDING)
            : BigDecimal.ZERO;
        BigDecimal completionRate = approvedLoans > 0
            ? BigDecimal.valueOf(100.0 * completedLoans / approvedLoans).setScale(SCALE, ROUNDING)
            : BigDecimal.ZERO;

        return new DashboardResponse(
            totalLoans,
            approvedLoans,
            pendingLoans,
            defaultedLoans,
            completedLoans,
            totalLoanAmount.setScale(SCALE, ROUNDING),
            averageLoanAmount,
            totalInterestProjected.setScale(SCALE, ROUNDING),
            totalRepayments,
            onTimeRepayments,
            lateRepayments,
            missedRepayments,
            totalAmountCollected.setScale(SCALE, ROUNDING),
            totalPenaltiesCollected.setScale(SCALE, ROUNDING),
            onTimePaymentRate,
            totalUsers,
            averageCreditScore,
            lowRiskUsers,
            mediumRiskUsers,
            highRiskUsers,
            totalInsurances,
            activeInsurances,
            cancelledInsurances,
            totalPremiumsCollected.setScale(SCALE, ROUNDING),
            insuranceByType,
            defaultRate,
            completionRate,
            LocalDate.now()
        );
    }

    public UserDashboardResponse getUserDashboard(Long userId) {
        List<Loan> userLoans = loanRepository.findByUserId(userId);
        List<LoanResponse> loans = userLoans.stream().map(loanService::toResponse).toList();
        long totalUserLoans = userLoans.size();
        BigDecimal totalBorrowed = userLoans.stream()
            .map(l -> BigDecimal.valueOf(l.getAmount()))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        CreditScore cs = creditScoreRepository.findByUserId(userId).orElse(null);
        Integer creditScore = cs != null ? cs.getScore() : null;
        String riskLevel = CreditScoreResponse.computeRiskLevel(creditScore);
        Integer latePayments = cs != null ? cs.getLatePayments() : 0;

        List<LoanRepayment> userRepayments = repaymentRepository.findByUserId(userId);
        BigDecimal totalPaid = userRepayments.stream()
            .map(LoanRepayment::getAmountPaid)
            .filter(java.util.Objects::nonNull)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalPenalties = userRepayments.stream()
            .map(LoanRepayment::getPenaltyAmount)
            .filter(java.util.Objects::nonNull)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        long onTimeCount = userRepayments.stream().filter(r -> ON_TIME.equals(r.getPaymentStatus())).count();
        double personalOnTimeRate = userRepayments.isEmpty() ? 0.0 : (100.0 * onTimeCount / userRepayments.size());

        List<MicroInsuranceResponse> activeInsurances = microInsuranceService.getByUser(userId).stream()
            .filter(r -> ACTIVE.equals(r.status()))
            .toList();
        BigDecimal totalPremiums = activeInsurances.stream()
            .map(MicroInsuranceResponse::premium)
            .filter(java.util.Objects::nonNull)
            .map(BigDecimal::valueOf)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        String profileSummary;
        int scoreVal = creditScore != null ? creditScore : 0;
        if (personalOnTimeRate >= 90 && scoreVal >= 700) profileSummary = "EXCELLENT";
        else if (personalOnTimeRate >= 70 && scoreVal >= 500) profileSummary = "GOOD STANDING";
        else if (personalOnTimeRate >= 50 && scoreVal >= 400) profileSummary = "AT RISK";
        else profileSummary = "CRITICAL";

        return new UserDashboardResponse(
            userId,
            loans,
            totalUserLoans,
            totalBorrowed.setScale(SCALE, ROUNDING),
            creditScore,
            riskLevel,
            latePayments != null ? latePayments : 0,
            totalPaid.setScale(SCALE, ROUNDING),
            totalPenalties.setScale(SCALE, ROUNDING),
            personalOnTimeRate,
            activeInsurances,
            totalPremiums.setScale(SCALE, ROUNDING),
            profileSummary
        );
    }
}
