package com.rayen.walletManagement.controller;

import com.rayen.walletManagement.entity.Wallet;
import com.rayen.walletManagement.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    // POST /api/v1/wallets
    @PostMapping
    public ResponseEntity<Wallet> createWallet() {
        return ResponseEntity.status(HttpStatus.CREATED).body(walletService.createWallet());
    }

    // GET /api/v1/wallets/me
    @GetMapping("/me")
    public ResponseEntity<Wallet> getMyWallet() {
        return ResponseEntity.ok(walletService.getMyWallet());
    }

    // GET /api/v1/wallets/{walletId}
    @GetMapping("/{walletId}")
    public ResponseEntity<Wallet> getWalletById(@PathVariable Long walletId) {
        return ResponseEntity.ok(walletService.getWalletById(walletId));
    }

    // GET /api/v1/wallets
    @GetMapping
    public ResponseEntity<List<Wallet>> getAllWallets() {
        return ResponseEntity.ok(walletService.getAllWallets());
    }

    // DELETE /api/v1/wallets/me
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMyWallet() {
        walletService.deleteMyWallet();
        return ResponseEntity.noContent().build();
    }

    // POST /api/v1/wallets/convert?amount=100
    @PostMapping("/convert")
    public ResponseEntity<Wallet> convertDinarsToEqua(@RequestParam BigDecimal amount) {
        return ResponseEntity.ok(walletService.convertDinarsToEqua(amount));
    }
}