package com.rayen.loanManagement.service;

import com.rayen.loanManagement.entity.CreditScore;

import java.util.List;

public interface ICreditScoreService {

    List<CreditScore> getAllCreditScore();
    CreditScore getCreditScoreById(Long idCreditScore);
    CreditScore addCreditScore(CreditScore creditScore);
    void removeCreditScore(Long idCreditScore);
    CreditScore modifyCreditScore(CreditScore creditScore);
}
