package com.rayen.blockChainManagement.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.rayen.blockChainManagement.entity.Node;
import com.rayen.blockChainManagement.model.TransactionRequest;
import com.rayen.blockChainManagement.model.TransactionResponse;
import com.rayen.blockChainManagement.service.SmartContract;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/v1/smartContract")
@RequiredArgsConstructor
@Slf4j
public class SmartContractController {
    private final SmartContract smartContract;

    @PostMapping("/process")
    public ResponseEntity<TransactionResponse> processTransaction(@RequestBody TransactionRequest request)
            throws BadRequestException, JsonProcessingException, ExecutionException, InterruptedException {
        log.info("Processing transaction from wallet: {}", request.getFromWallet());
        TransactionResponse response = smartContract.processTransaction(request);
        return ResponseEntity.ok(response);
    }
    @GetMapping
    public ResponseEntity<List<Node>> getAllNodes() {
        log.info("REST request to get all nodes with blockchain");
        return ResponseEntity.ok(smartContract.getAllNodesWithBlockchain());
    }

}
