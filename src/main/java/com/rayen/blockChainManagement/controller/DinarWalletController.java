package com.rayen.blockChainManagement.controller;

import com.rayen.blockChainManagement.entity.Dinar;
import com.rayen.blockChainManagement.entity.DinarWallet;
import com.rayen.blockChainManagement.service.DinarWalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/dinar-wallets")
@RequiredArgsConstructor
public class DinarWalletController {

    private final DinarWalletService dinarWalletService;

    @PostMapping("/create/{userId}")
    public ResponseEntity<DinarWallet> createWallet(@PathVariable String userId) {
        return ResponseEntity.ok(dinarWalletService.createWallet(userId));
    }

    @PostMapping("/{walletId}/deposit/{amount}")
    public ResponseEntity<DinarWallet> deposit(@PathVariable String walletId,
                                               @PathVariable int amount) {
        return ResponseEntity.ok(dinarWalletService.deposit(walletId, amount));
    }

    @GetMapping("/{walletId}")
    public ResponseEntity<DinarWallet> getWallet(@PathVariable String walletId) {
        return ResponseEntity.ok(dinarWalletService.getWallet(walletId));
    }

    @GetMapping("/{walletId}/dinars")
    public ResponseEntity<List<Dinar>> getWalletDinars(@PathVariable String walletId) {
        return ResponseEntity.ok(dinarWalletService.getWalletDinars(walletId));
    }

    @GetMapping("/node/{nodeId}/dinars")
    public ResponseEntity<List<Dinar>> getNodeDinars(@PathVariable Integer nodeId) {
        return ResponseEntity.ok(dinarWalletService.getNodeDinars(nodeId));
    }
}