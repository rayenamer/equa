package com.rayen.loanManagement.service;

import com.rayen.loanManagement.entity.Loan;
import com.rayen.loanManagement.entity.LoanAmortization;
import com.rayen.loanManagement.entity.LoanRepayment;
import com.rayen.loanManagement.model.RepaymentRequest;
import com.rayen.loanManagement.model.RepaymentResponse;
import com.rayen.loanManagement.model.RepaymentSummary;
import com.rayen.loanManagement.repository.LoanAmortizationRepository;
import com.rayen.loanManagement.repository.LoanRepository;
import com.rayen.loanManagement.repository.LoanRepaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanRepaymentService {

    private static final int SCALE = 2;
    private static final RoundingMode ROUNDING = RoundingMode.HALF_EVEN;
    private static final String PAID = "PAID";
    private static final String ON_TIME = "ON_TIME";
    private static final String LATE = "LATE";
    private static final String PARTIAL = "PARTIAL";
    private static final String MISSED = "MISSED";
    private static final String COMPLETED = "COMPLETED";

    private final LoanRepaymentRepository repaymentRepository;
    private final LoanRepository loanRepository;
    private final LoanAmortizationRepository amortizationRepository;
    private final ICreditScoreService creditScoreService;

    @Transactional
    public RepaymentResponse makePayment(RepaymentRequest req) {
        Loan loan = loanRepository.findById(req.loanId())
            .orElseThrow(() -> new IllegalArgumentException("Loan not found"));
        LoanAmortization amortRow = amortizationRepository.findByLoan_LoanIdAndPeriodNumber(req.loanId(), req.periodNumber())
            .orElseThrow(() -> new IllegalArgumentException("Amortization period not found"));

        if (PAID.equals(amortRow.getStatus())) {
            throw new IllegalArgumentException("Period already paid");
        }

        BigDecimal expectedAmount = loan.getMonthlyPayment() != null ? loan.getMonthlyPayment() : amortRow.getPayment();
        LocalDate dueDate = amortRow.getPeriodDate();

        String paymentStatus;
        BigDecimal penaltyAmount;
        String message;

        if (req.amountPaid().compareTo(expectedAmount) >= 0) {
            boolean onTime = !req.paymentDate().isAfter(dueDate);
            if (onTime) {
                paymentStatus = ON_TIME;
                penaltyAmount = BigDecimal.ZERO.setScale(SCALE, ROUNDING);
                message = "Payment accepted";
            } else {
                paymentStatus = LATE;
                long daysLate = ChronoUnit.DAYS.between(dueDate, req.paymentDate());
                double penaltyRate = Math.min(0.001 * daysLate, 0.10);
                penaltyAmount = expectedAmount.multiply(BigDecimal.valueOf(penaltyRate)).setScale(SCALE, ROUNDING);
                message = "Late penalty applied: " + penaltyAmount;
            }
        } else {
            paymentStatus = PARTIAL;
            penaltyAmount = expectedAmount.multiply(BigDecimal.valueOf(0.05)).setScale(SCALE, ROUNDING);
            message = "Partial payment; 5% penalty applied: " + penaltyAmount;
        }

        LoanRepayment repayment = LoanRepayment.builder()
            .loan(loan)
            .userId(req.userId())
            .periodNumber(req.periodNumber())
            .amountPaid(req.amountPaid().setScale(SCALE, ROUNDING))
            .expectedAmount(expectedAmount)
            .penaltyAmount(penaltyAmount)
            .dueDate(dueDate)
            .paymentDate(req.paymentDate())
            .paymentStatus(paymentStatus)
            .build();
        repayment = repaymentRepository.save(repayment);

        amortRow.setStatus(PAID);
        amortizationRepository.save(amortRow);

        if (ON_TIME.equals(paymentStatus)) {
            creditScoreService.evaluateCreditScore(req.userId());
            creditScoreService.addScoreBonus(req.userId(), 5);
        } else {
            creditScoreService.addLatePayment(req.userId());
            creditScoreService.evaluateCreditScore(req.userId());
        }

        List<LoanAmortization> allPeriods = amortizationRepository.findByLoan_LoanIdOrderByPeriodNumberAsc(req.loanId());
        boolean allPaid = allPeriods.stream().allMatch(p -> PAID.equals(p.getStatus()));
        if (allPaid) {
            loan.setStatus(COMPLETED);
            loanRepository.save(loan);
        }

        return toResponse(repayment, message);
    }

    public List<RepaymentResponse> getRepaymentsByLoan(Long loanId) {
        return repaymentRepository.findByLoan_LoanIdOrderByPeriodNumberAsc(loanId).stream()
            .map(r -> toResponse(r, null))
            .toList();
    }

    public List<RepaymentResponse> getRepaymentsByUser(Long userId) {
        return repaymentRepository.findByUserId(userId).stream()
            .map(r -> toResponse(r, null))
            .toList();
    }

    public RepaymentSummary getLoanRepaymentSummary(Long loanId) {
        Loan loan = loanRepository.findById(loanId).orElseThrow(() -> new IllegalArgumentException("Loan not found"));
        List<LoanRepayment> repayments = repaymentRepository.findByLoan_LoanIdOrderByPeriodNumberAsc(loanId);
        List<LoanAmortization> periods = amortizationRepository.findByLoan_LoanIdOrderByPeriodNumberAsc(loanId);

        BigDecimal totalPaid = repayments.stream()
            .map(LoanRepayment::getAmountPaid)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalPenalties = repayments.stream()
            .map(LoanRepayment::getPenaltyAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        int paidPeriods = (int) periods.stream().filter(p -> PAID.equals(p.getStatus())).count();
        int totalPeriods = periods.size();
        int remainingPeriods = Math.max(0, totalPeriods - paidPeriods);

        BigDecimal expectedTotal = loan.getMonthlyPayment() != null && totalPeriods > 0
            ? loan.getMonthlyPayment().multiply(BigDecimal.valueOf(totalPeriods))
            : BigDecimal.ZERO;
        BigDecimal totalPending = expectedTotal.subtract(totalPaid).max(BigDecimal.ZERO);

        double completionPercent = totalPeriods > 0 ? (100.0 * paidPeriods / totalPeriods) : 0.0;

        return new RepaymentSummary(
            loanId,
            totalPaid.setScale(SCALE, ROUNDING),
            totalPending.setScale(SCALE, ROUNDING),
            totalPenalties.setScale(SCALE, ROUNDING),
            paidPeriods,
            remainingPeriods,
            completionPercent,
            loan.getStatus()
        );
    }

    private RepaymentResponse toResponse(LoanRepayment r, String message) {
        return new RepaymentResponse(
            r.getRepaymentId(),
            r.getLoan() != null ? r.getLoan().getLoanId() : null,
            r.getUserId(),
            r.getPeriodNumber(),
            r.getAmountPaid(),
            r.getExpectedAmount(),
            r.getPenaltyAmount(),
            r.getDueDate(),
            r.getPaymentDate(),
            r.getPaymentStatus(),
            message
        );
    }
}
