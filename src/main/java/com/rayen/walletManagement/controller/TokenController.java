package com.rayen.walletManagement.controller;

import com.rayen.walletManagement.model.TokenDTO;
import com.rayen.walletManagement.service.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tokens")
@Tag(name = "Token Management", description = "CRUD & business operations for tokens")
public class TokenController {

    @Autowired
    private TokenService tokenService;

    // ==================== CRUD ====================

    @GetMapping
    @Operation(summary = "Get all tokens")
    public ResponseEntity<List<TokenDTO>> getAllTokens() {
        return ResponseEntity.ok(tokenService.getAllTokens());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get token by ID")
    public ResponseEntity<TokenDTO> getTokenById(@PathVariable Long id) {
        return ResponseEntity.ok(tokenService.getTokenById(id));
    }

    @GetMapping("/wallet/{walletId}")
    @Operation(summary = "Get tokens by wallet ID")
    public ResponseEntity<List<TokenDTO>> getTokensByWalletId(@PathVariable Long walletId) {
        return ResponseEntity.ok(tokenService.getTokensByWalletId(walletId));
    }

    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Get tokens by customer ID")
    public ResponseEntity<List<TokenDTO>> getTokensByCustomerId(@PathVariable Long customerId) {
        return ResponseEntity.ok(tokenService.getTokensByCustomerId(customerId));
    }

    @PostMapping
    @Operation(summary = "Create a new token")
    public ResponseEntity<TokenDTO> createToken(@RequestBody TokenDTO tokenDTO) {
        return new ResponseEntity<>(tokenService.createToken(tokenDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update token")
    public ResponseEntity<TokenDTO> updateToken(@PathVariable Long id, @RequestBody TokenDTO tokenDTO) {
        return ResponseEntity.ok(tokenService.updateToken(id, tokenDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete token")
    public ResponseEntity<Void> deleteToken(@PathVariable Long id) {
        tokenService.deleteToken(id);
        return ResponseEntity.noContent().build();
    }

    // ==================== BUSINESS OPERATIONS ====================

    @PostMapping("/{tokenId}/transfer")
    @Operation(summary = "Transfer token value to another customer with conversion")
    public ResponseEntity<TokenDTO> transferToken(
            @PathVariable Long tokenId,
            @RequestParam Long recipientCustomerId,
            @RequestParam Double amount) {
        return ResponseEntity.ok(tokenService.transferToken(tokenId, recipientCustomerId, amount));
    }

    @GetMapping("/wallet/{walletId}/total-value")
    @Operation(summary = "Get total converted token value for a wallet")
    public ResponseEntity<Double> getTotalTokenValue(@PathVariable Long walletId) {
        return ResponseEntity.ok(tokenService.getTotalTokenValue(walletId));
    }
}
