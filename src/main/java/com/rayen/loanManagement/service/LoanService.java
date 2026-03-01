package com.rayen.loanManagement.service;

import com.rayen.loanManagement.entity.Loan;
import com.rayen.loanManagement.entity.LoanAmortization;
import com.rayen.loanManagement.model.AmortizationRowResponse;
import com.rayen.loanManagement.model.LoanResponse;
import com.rayen.loanManagement.repository.LoanAmortizationRepository;
import com.rayen.loanManagement.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoanService implements ILoanService {

    private static final int SCALE = 2;
    private static final RoundingMode ROUNDING = RoundingMode.HALF_EVEN;

    private final LoanRepository loanRepository;
    private final LoanAmortizationRepository amortRepo;

    @Override
    public List<Loan> getAllLoan() {
        return loanRepository.findAll();
    }

    @Override
    public Loan getLoanById(Long idLoan) {
        return loanRepository.findById(idLoan).orElse(null);
    }

    @Override
    public Loan addLoan(Loan loan) {
        return loanRepository.save(loan);
    }

    @Override
    public void removeLoan(Long idLoan) {
        loanRepository.deleteById(idLoan);
    }

    @Override
    public Loan modifyLoan(Loan loan) {
        return loanRepository.save(loan);
    }

    public BigDecimal computeMonthlyPayment(float amount, float annualRatePercent, int months) {
        BigDecimal M = BigDecimal.valueOf(amount);
        if (months <= 0) {
            return M.setScale(SCALE, ROUNDING);
        }
        BigDecimal i = BigDecimal.valueOf(annualRatePercent).divide(BigDecimal.valueOf(1200), 20, ROUNDING); // annualRate/12/100
        if (i.compareTo(BigDecimal.ZERO) == 0) {
            return M.divide(BigDecimal.valueOf(months), SCALE, ROUNDING);
        }
        // payment = M * [i(1+i)^n] / [(1+i)^n - 1]
        BigDecimal onePlusI = BigDecimal.ONE.add(i);
        BigDecimal onePlusIPowN = onePlusI.pow(months);
        BigDecimal numerator = i.multiply(onePlusIPowN);
        BigDecimal denominator = onePlusIPowN.subtract(BigDecimal.ONE);
        return M.multiply(numerator).divide(denominator, SCALE, ROUNDING);
    }

    public void approveLoan(Long loanId) {
        Loan loan = getEntity(loanId);
        if (loan == null) return;
        loan.setStatus("APPROVED");
        loan.setStartDate(LocalDate.now());
        if (loan.getDurationMonths() != null) {
            loan.setMonthlyPayment(computeMonthlyPayment(loan.getAmount(), loan.getInterestRate(), loan.getDurationMonths()));
        }
        loanRepository.save(loan);
    }

    @Transactional
    public List<AmortizationRowResponse> generateAmortization(Long loanId) {
        Loan loan = getEntity(loanId);
        if (loan == null || loan.getDurationMonths() == null || loan.getDurationMonths() <= 0) {
            return List.of();
        }
        amortRepo.deleteByLoan_LoanId(loanId);

        BigDecimal principal = BigDecimal.valueOf(loan.getAmount()).setScale(SCALE, ROUNDING);
        BigDecimal monthlyRate = BigDecimal.valueOf(loan.getInterestRate()).divide(BigDecimal.valueOf(1200), 20, ROUNDING);
        BigDecimal payment = loan.getMonthlyPayment() != null
            ? loan.getMonthlyPayment()
            : computeMonthlyPayment(loan.getAmount(), loan.getInterestRate(), loan.getDurationMonths());
        LocalDate startDate = loan.getStartDate() != null ? loan.getStartDate() : LocalDate.now();
        int n = loan.getDurationMonths();

        for (int k = 1; k <= n; k++) {
            BigDecimal startingBalance = principal;
            BigDecimal interest = startingBalance.multiply(monthlyRate).setScale(SCALE, ROUNDING);
            boolean isLast = (k == n);
            BigDecimal principalPaid;
            BigDecimal endingBalance;
            BigDecimal paymentThisPeriod;
            if (isLast) {
                principalPaid = startingBalance;
                endingBalance = BigDecimal.ZERO.setScale(SCALE, ROUNDING);
                paymentThisPeriod = interest.add(principalPaid).setScale(SCALE, ROUNDING);
            } else {
                principalPaid = payment.subtract(interest).setScale(SCALE, ROUNDING);
                endingBalance = startingBalance.subtract(principalPaid).setScale(SCALE, ROUNDING);
                paymentThisPeriod = payment;
            }

            LoanAmortization row = LoanAmortization.builder()
                .loan(loan)
                .periodNumber(k)
                .periodDate(startDate.plusMonths(k - 1))
                .startingBalance(startingBalance)
                .interest(interest)
                .principal(principalPaid)
                .payment(paymentThisPeriod)
                .endingBalance(endingBalance)
                .status("PENDING")
                .build();
            amortRepo.save(row);
            principal = endingBalance;
        }

        return getAmortization(loanId);
    }

    public List<AmortizationRowResponse> getAmortization(Long loanId) {
        return amortRepo.findByLoan_LoanIdOrderByPeriodNumberAsc(loanId).stream()
            .map(this::toAmortizationRowResponse)
            .collect(Collectors.toList());
    }

    public LoanResponse toResponse(Loan loan) {
        if (loan == null) return null;
        return new LoanResponse(
            loan.getLoanId(),
            loan.getUserId(),
            loan.getAmount(),
            loan.getInterestRate(),
            loan.getDurationMonths(),
            loan.getStatus(),
            loan.getStartDate(),
            loan.getDueDate(),
            loan.getMonthlyPayment()
        );
    }

    public Loan getEntity(Long loanId) {
        return loanRepository.findById(loanId).orElse(null);
    }

    private AmortizationRowResponse toAmortizationRowResponse(LoanAmortization a) {
        return new AmortizationRowResponse(
            a.getPeriodNumber(),
            a.getPeriodDate(),
            a.getStartingBalance(),
            a.getInterest(),
            a.getPrincipal(),
            a.getPayment(),
            a.getEndingBalance(),
            a.getStatus()
        );
    }
}
