package com.rayen.blockChainManagement.controller;

import com.rayen.blockChainManagement.dto.MouvementRequest;
import com.rayen.blockChainManagement.dto.MouvementResponse;
import com.rayen.blockChainManagement.service.MouvementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/mouvements")
@CrossOrigin(origins = "http://localhost:4200")
public class MouvementController {

    private final MouvementService mouvementService;

    @Autowired
    public MouvementController(MouvementService mouvementService) {
        this.mouvementService = mouvementService;
    }

    @GetMapping("/business/{businessId}")
    public ResponseEntity<List<MouvementResponse>> getMouvements(@PathVariable Long businessId) {
        return ResponseEntity.ok(mouvementService.getMouvementsByBusiness(businessId));
    }

    @PostMapping("/business/{businessId}")
    public ResponseEntity<MouvementResponse> addMouvement(@PathVariable Long businessId,
            @RequestBody MouvementRequest request) {
        try {
            return ResponseEntity.ok(mouvementService.addMouvement(businessId, request));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}/classify")
    public ResponseEntity<MouvementResponse> classifyMouvement(
            @PathVariable Long id,
            @RequestParam String statut,
            @RequestParam(required = false) String compte,
            @RequestParam(required = false) String categorie) {
        try {
            return ResponseEntity.ok(mouvementService.updateMouvementStatus(id, statut, compte, categorie));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
