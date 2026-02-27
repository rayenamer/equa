package com.rayen.blockChainManagement.model;


import com.rayen.blockChainManagement.entity.Transaction;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {

    public TransactionResponse toResponse(Transaction transaction) {
        if (transaction == null) {
            return null;
        }

        return TransactionResponse.builder()
                .transactionId(transaction.getTransactionId())
                .fromWallet(transaction.getFromWallet())
                .toWallet(transaction.getToWallet())
                .amount(transaction.getAmount())
                .timestamp(transaction.getTimestamp())
                .status(transaction.getStatus())
                .transactionHash(transaction.getTransactionHash())
                .fee(transaction.getFee())
                .build();
    }

    public TransactionResponse toResponseWithoutRelations(Transaction transaction) {
        if (transaction == null) {
            return null;
        }

        return TransactionResponse.builder()
                .transactionId(transaction.getTransactionId())
                .fromWallet(transaction.getFromWallet())
                .toWallet(transaction.getToWallet())
                .amount(transaction.getAmount())
                .timestamp(transaction.getTimestamp())
                .status(transaction.getStatus())
                .transactionHash(transaction.getTransactionHash())
                .fee(transaction.getFee())
                .build();
    }

}