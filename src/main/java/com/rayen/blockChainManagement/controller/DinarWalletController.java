package com.rayen.blockChainManagement.controller;

import com.rayen.blockChainManagement.entity.Dinar;
import com.rayen.blockChainManagement.entity.DinarWallet;
import com.rayen.blockChainManagement.service.DinarWalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/dinar-wallets")
@RequiredArgsConstructor
public class DinarWalletController {

    private final DinarWalletService dinarWalletService;

    @PostMapping("/create")
    public ResponseEntity<DinarWallet> createWallet() {
        return ResponseEntity.ok(dinarWalletService.createWallet());
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

    @PostMapping("/{walletId}/withdraw/{amount}")
    public ResponseEntity<DinarWallet> withdraw(@PathVariable String walletId,
                                                @PathVariable int amount) throws BadRequestException {
        return ResponseEntity.ok(dinarWalletService.withdraw(walletId, amount));
    }

    @GetMapping("/myWallet")
    public ResponseEntity<DinarWallet> getMyWallet()
    {
        return ResponseEntity.ok((dinarWalletService.getMyWallet()));
    }
}