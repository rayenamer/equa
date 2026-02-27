package com.rayen.blockChainManagement.service;


import com.rayen.blockChainManagement.model.TransactionRequest;
import com.rayen.blockChainManagement.model.TransactionResponse;
import com.rayen.blockChainManagement.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {
    private final TransactionRepository TransactionRepository;

    //26 letters × 10 digits = **260 possible combinations**
    private String generateHash() {
        Random random = new Random();
        char letter = (char) ('a' + random.nextInt(26));
        int number = random.nextInt(10);
        return String.valueOf(letter) + number;
    }
}
