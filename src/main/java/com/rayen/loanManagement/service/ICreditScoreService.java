package com.rayen.loanManagement.service;

import com.rayen.loanManagement.entity.CreditScore;
import com.rayen.loanManagement.model.CreditScoreResponse;

import java.util.List;

public interface ICreditScoreService {

    List<CreditScore> getAllCreditScore();
    CreditScore getCreditScoreById(Long idCreditScore);
    CreditScore addCreditScore(CreditScore creditScore);
    void removeCreditScore(Long idCreditScore);
    CreditScore modifyCreditScore(CreditScore creditScore);

    CreditScoreResponse evaluateCreditScore(Long userId);
    CreditScoreResponse getScoreByUserId(Long userId);
    void addLatePayment(Long userId);
    void addScoreBonus(Long userId, int bonus);
}
