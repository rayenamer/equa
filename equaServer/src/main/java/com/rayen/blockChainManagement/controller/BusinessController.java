package com.rayen.blockChainManagement.controller;

import com.rayen.blockChainManagement.dto.BusinessRequest;
import com.rayen.blockChainManagement.dto.BusinessResponse;
import com.rayen.blockChainManagement.dto.BusinessWalletResponse;
import com.rayen.blockChainManagement.entity.Business;
import com.rayen.blockChainManagement.service.BusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/business")
@CrossOrigin(origins = "http://localhost:4200")
public class BusinessController {

    private final BusinessService businessService;
    private final com.rayen.walletManagement.repository.BusinessWalletRepository businessWalletRepository;

    @Autowired
    public BusinessController(BusinessService businessService,
            com.rayen.walletManagement.repository.BusinessWalletRepository businessWalletRepository) {
        this.businessService = businessService;
        this.businessWalletRepository = businessWalletRepository;
    }

    @PostMapping("/user/{userId}")
    public ResponseEntity<BusinessResponse> createBusiness(@PathVariable Long userId,
            @RequestBody BusinessRequest request) {
        try {
            Business business = new Business();
            business.setName(request.getName());
            business.setIndustry(request.getIndustry());
            business.setRegistrationNumber(request.getRegistrationNumber());
            Business saved = businessService.createBusiness(userId, business);
            BusinessResponse dto = new BusinessResponse();
            dto.setId(saved.getId());
            dto.setName(saved.getName());
            dto.setIndustry(saved.getIndustry());
            dto.setRegistrationNumber(saved.getRegistrationNumber());
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BusinessResponse>> getBusinessesByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(businessService.getBusinessesByUser(userId));
    }

    private BusinessWalletResponse toWalletDto(com.rayen.walletManagement.entity.BusinessWallet w) {
        BusinessWalletResponse dto = new BusinessWalletResponse();
        dto.setWalletId(w.getWalletId());
        dto.setBusinessId(w.getBusiness().getId());
        dto.setStatus(w.getStatus());
        dto.setEquaAmount(w.getEquaAmount());
        dto.setLastActivityAt(w.getLastActivityAt());
        return dto;
    }

    @GetMapping("/{businessId}/wallet")
    public ResponseEntity<BusinessWalletResponse> getBusinessWallet(@PathVariable Long businessId) {
        return businessWalletRepository.findByBusinessId(businessId)
                .map(w -> ResponseEntity.ok(toWalletDto(w)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/{businessId}/wallet")
    public ResponseEntity<BusinessWalletResponse> createBusinessWallet(@PathVariable Long businessId) {
        // Return existing wallet if already created
        java.util.Optional<com.rayen.walletManagement.entity.BusinessWallet> existing = businessWalletRepository
                .findByBusinessId(businessId);
        if (existing.isPresent()) {
            return ResponseEntity.ok(toWalletDto(existing.get()));
        }
        return businessService.getBusinessById(businessId).map(business -> {
            com.rayen.walletManagement.entity.BusinessWallet wallet = new com.rayen.walletManagement.entity.BusinessWallet();
            wallet.setBusiness(business);
            wallet.setStatus("ACTIVE");
            wallet.setEquaAmount(0f);
            wallet.setLastActivityAt(java.time.LocalDateTime.now());
            return ResponseEntity.ok(toWalletDto(businessWalletRepository.save(wallet)));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{businessId}/finance")
    public ResponseEntity<java.util.Map<String, Object>> getFinanceRatios(@PathVariable Long businessId) {
        java.util.Map<String, Object> data = new java.util.HashMap<>();

        java.util.List<java.util.Map<String, Object>> liquidite = new java.util.ArrayList<>();
        liquidite.add(java.util.Map.of("nom", "Ratio de liquidité générale", "formule",
                "Actif courant / Passif courant", "valeur", 2.34, "seuil", "> 1", "statut", "bon", "description",
                "Mesure la capacité à honorer les dettes à court terme."));
        data.put("ratiosLiquidite", liquidite);

        java.util.List<java.util.Map<String, Object>> solvabilite = new java.util.ArrayList<>();
        solvabilite.add(java.util.Map.of("nom", "Ratio d'endettement", "formule", "Dettes totales / Capitaux propres",
                "valeur", 0.65, "seuil", "< 1", "statut", "bon", "description",
                "Structure financière et dépendance aux créanciers."));
        data.put("ratiosSolvabilite", solvabilite);

        java.util.List<java.util.Map<String, Object>> profitabilite = new java.util.ArrayList<>();
        profitabilite.add(java.util.Map.of("nom", "Marge bénéficiaire nette", "formule", "Résultat net / CA", "valeur",
                "12.4%", "seuil", "", "statut", "bon", "description",
                "Part du chiffre d'affaires convertie en profit."));
        profitabilite.add(java.util.Map.of("nom", "ROE (Return on Equity)", "formule",
                "Résultat net / Capitaux propres", "valeur", "18.7%", "seuil", "", "statut", "bon", "description",
                "Rentabilité des capitaux propres investis."));
        data.put("ratiosProfitabilite", profitabilite);

        return ResponseEntity.ok(data);
    }
}
