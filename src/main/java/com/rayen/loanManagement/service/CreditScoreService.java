package com.rayen.loanManagement.service;

import com.rayen.loanManagement.entity.CreditScore;
import com.rayen.loanManagement.repository.CreditScoreRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CreditScoreService implements ICreditScoreService {

    private final CreditScoreRepository creditScoreRepository;

    public CreditScoreService(CreditScoreRepository creditScoreRepository) {
        this.creditScoreRepository = creditScoreRepository;
    }

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
}
