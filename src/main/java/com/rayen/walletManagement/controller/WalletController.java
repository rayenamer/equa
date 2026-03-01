package com.rayen.walletManagement.controller;

import com.rayen.walletManagement.model.RiskAssessmentDTO;
import com.rayen.walletManagement.model.TransferRequest;
import com.rayen.walletManagement.model.WalletDTO;
import com.rayen.walletManagement.service.RiskAssessmentService;
import com.rayen.walletManagement.service.WalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wallets")
@Tag(name = "Wallet Management", description = "CRUD & business operations for wallets")
public class WalletController {

    @Autowired
    private WalletService walletService;

    @Autowired
    private RiskAssessmentService riskAssessmentService;

    // ==================== CRUD ====================

    @GetMapping
    @Operation(summary = "Get all wallets")
    public ResponseEntity<List<WalletDTO>> getAllWallets() {
        return ResponseEntity.ok(walletService.getAllWallets());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get wallet by ID")
    public ResponseEntity<WalletDTO> getWalletById(@PathVariable Long id) {
        return ResponseEntity.ok(walletService.getWalletById(id));
    }

    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Get wallet by customer ID")
    public ResponseEntity<WalletDTO> getWalletByCustomerId(@PathVariable Long customerId) {
        return ResponseEntity.ok(walletService.getWalletByCustomerId(customerId));
    }

    @PostMapping
    @Operation(summary = "Create a new wallet")
    public ResponseEntity<WalletDTO> createWallet(@RequestBody WalletDTO walletDTO) {
        return new ResponseEntity<>(walletService.createWallet(walletDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update wallet")
    public ResponseEntity<WalletDTO> updateWallet(@PathVariable Long id, @RequestBody WalletDTO walletDTO) {
        return ResponseEntity.ok(walletService.updateWallet(id, walletDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete wallet")
    public ResponseEntity<Void> deleteWallet(@PathVariable Long id) {
        walletService.deleteWallet(id);
        return ResponseEntity.noContent().build();
    }

    // ==================== BUSINESS OPERATIONS ====================

    @PostMapping("/{id}/deposit")
    @Operation(summary = "Deposit funds into wallet")
    public ResponseEntity<WalletDTO> deposit(@PathVariable Long id, @RequestParam Double amount) {
        return ResponseEntity.ok(walletService.deposit(id, amount));
    }

    @PostMapping("/{id}/withdraw")
    @Operation(summary = "Withdraw funds from wallet")
    public ResponseEntity<WalletDTO> withdraw(@PathVariable Long id, @RequestParam Double amount) {
        return ResponseEntity.ok(walletService.withdraw(id, amount));
    }

    @PostMapping("/{id}/transfer")
    @Operation(summary = "Transfer tokens to another wallet")
    public ResponseEntity<WalletDTO> transferTokens(@PathVariable Long id, @RequestBody TransferRequest request) {
        return ResponseEntity.ok(walletService.transferTokens(id, request));
    }

    @PutMapping("/{id}/suspend")
    @Operation(summary = "Suspend wallet")
    public ResponseEntity<WalletDTO> suspendWallet(@PathVariable Long id) {
        return ResponseEntity.ok(walletService.suspendWallet(id));
    }

    @PutMapping("/{id}/activate")
    @Operation(summary = "Activate wallet")
    public ResponseEntity<WalletDTO> activateWallet(@PathVariable Long id) {
        return ResponseEntity.ok(walletService.activateWallet(id));
    }

    @GetMapping("/low-balance")
    @Operation(summary = "Get wallets with balance below threshold")
    public ResponseEntity<List<WalletDTO>> getLowBalanceWallets(@RequestParam(defaultValue = "100") Double threshold) {
        return ResponseEntity.ok(walletService.getLowBalanceWallets(threshold));
    }

    // ==================== RISK & AI SCORING ====================

    @GetMapping("/risk/{customerId}")
    @Operation(summary = "AI-based risk assessment for a customer")
    public ResponseEntity<RiskAssessmentDTO> assessRisk(@PathVariable Long customerId) {
        return ResponseEntity.ok(riskAssessmentService.assessRisk(customerId));
    }
}
