package com.rayen.loanManagement.service;

import com.rayen.loanManagement.entity.CreditScore;
import com.rayen.loanManagement.entity.Loan;
import com.rayen.loanManagement.entity.MicroInsurance;
import com.rayen.loanManagement.model.CreditScoreResponse;
import com.rayen.loanManagement.repository.CreditScoreRepository;
import com.rayen.loanManagement.repository.LoanRepository;
import com.rayen.loanManagement.repository.MicroInsuranceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CreditScoreService implements ICreditScoreService {

    private static final int BASE_SCORE = 500;
    private static final String APPROVED = "APPROVED";
    private static final String DEFAULTED = "DEFAULTED";
    private static final String PENDING = "PENDING";
    private static final String ACTIVE = "ACTIVE";

    private final CreditScoreRepository creditScoreRepository;
    private final LoanRepository loanRepository;
    private final MicroInsuranceRepository microInsuranceRepository;

    @Override
    public List<CreditScore> getAllCreditScore() {
        return creditScoreRepository.findAll();
    }

    @Override
    public CreditScore getCreditScoreById(Long idCreditScore) {
        return creditScoreRepository.findById(idCreditScore).orElse(null);
    }

    @Override
    public CreditScore addCreditScore(CreditScore creditScore) {
        return creditScoreRepository.save(creditScore);
    }

    @Override
    public void removeCreditScore(Long idCreditScore) {
        creditScoreRepository.deleteById(idCreditScore);
    }

    @Override
    public CreditScore modifyCreditScore(CreditScore creditScore) {
        return creditScoreRepository.save(creditScore);
    }

    @Override
    @Transactional
    public CreditScoreResponse evaluateCreditScore(Long userId) {
        List<Loan> loans = loanRepository.findByUserId(userId);
        CreditScore creditScore = creditScoreRepository.findByUserId(userId)
            .orElseGet(() -> {
                CreditScore newScore = new CreditScore();
                newScore.setUserId(userId);
                newScore.setScore(BASE_SCORE);
                newScore.setLatePayments(0);
                newScore.setLastUpdated(LocalDate.now());
                return creditScoreRepository.save(newScore);
            });

        int baseScore = BASE_SCORE;

        for (Loan loan : loans) {
            if (APPROVED.equals(loan.getStatus())) {
                baseScore += 20;
            } else if (DEFAULTED.equals(loan.getStatus())) {
                creditScore.setLatePayments(creditScore.getLatePayments() + 1);
            }
        }

        for (Loan loan : loans) {
            if (!APPROVED.equals(loan.getStatus())) continue;
            List<MicroInsurance> activeInsurances = microInsuranceRepository
                .findByLoan_LoanIdAndStatus(loan.getLoanId(), ACTIVE);
            boolean hasActiveInsurance = false;
            for (MicroInsurance ins : activeInsurances) {
                hasActiveInsurance = true;
                switch (ins.getType() != null ? ins.getType().toUpperCase() : "") {
                    case "LIFE" -> baseScore += 15;
                    case "DISABILITY" -> baseScore += 10;
                    case "UNEMPLOYMENT" -> baseScore += 5;
                    default -> { }
                }
            }
            if (!hasActiveInsurance) {
                baseScore -= 5;
            }
        }

        baseScore -= creditScore.getLatePayments() * 10;
        creditScore.setScore(Math.max(0, baseScore));
        creditScore.setLastUpdated(LocalDate.now());
        creditScoreRepository.save(creditScore);
        return toResponse(creditScore);
    }

    @Override
    @Transactional(readOnly = true)
    public CreditScoreResponse getScoreByUserId(Long userId) {
        CreditScore creditScore = creditScoreRepository.findByUserId(userId)
            .orElseThrow(() -> new IllegalArgumentException("CreditScore not found"));
        return toResponse(creditScore);
    }

    @Override
    @Transactional
    public void addLatePayment(Long userId) {
        CreditScore cs = creditScoreRepository.findByUserId(userId)
            .orElseGet(() -> {
                CreditScore newScore = new CreditScore();
                newScore.setUserId(userId);
                newScore.setScore(BASE_SCORE);
                newScore.setLatePayments(0);
                newScore.setLastUpdated(LocalDate.now());
                return creditScoreRepository.save(newScore);
            });
        cs.setLatePayments(cs.getLatePayments() + 1);
        cs.setLastUpdated(LocalDate.now());
        creditScoreRepository.save(cs);
    }

    @Override
    @Transactional
    public void addScoreBonus(Long userId, int bonus) {
        creditScoreRepository.findByUserId(userId).ifPresent(cs -> {
            cs.setScore(cs.getScore() + bonus);
            cs.setLastUpdated(LocalDate.now());
            creditScoreRepository.save(cs);
        });
    }

    private CreditScoreResponse toResponse(CreditScore cs) {
        return new CreditScoreResponse(
            cs.getScoreId(),
            cs.getUserId(),
            cs.getScore(),
            cs.getLatePayments(),
            cs.getLastUpdated(),
            CreditScoreResponse.computeRiskLevel(cs.getScore())
        );
    }
}
