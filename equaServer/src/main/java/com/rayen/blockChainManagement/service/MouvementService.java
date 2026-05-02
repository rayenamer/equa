package com.rayen.blockChainManagement.service;

import com.rayen.blockChainManagement.dto.MouvementRequest;
import com.rayen.blockChainManagement.dto.MouvementResponse;
import com.rayen.blockChainManagement.entity.Business;
import com.rayen.blockChainManagement.entity.Mouvement;
import com.rayen.blockChainManagement.repository.BusinessRepository;
import com.rayen.blockChainManagement.repository.MouvementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MouvementService {

    private final MouvementRepository mouvementRepository;
    private final BusinessRepository businessRepository;

    @Autowired
    public MouvementService(MouvementRepository mouvementRepository, BusinessRepository businessRepository) {
        this.mouvementRepository = mouvementRepository;
        this.businessRepository = businessRepository;
    }

    private MouvementResponse toDto(Mouvement m) {
        MouvementResponse dto = new MouvementResponse();
        dto.setId(m.getId());
        dto.setDate(m.getDate());
        dto.setLibelle(m.getLibelle());
        dto.setType(m.getType());
        dto.setCompte(m.getCompte());
        dto.setMontant(m.getMontant());
        dto.setCategorie(m.getCategorie());
        dto.setStatut(m.getStatut());
        return dto;
    }

    public List<MouvementResponse> getMouvementsByBusiness(Long businessId) {
        return mouvementRepository.findByBusinessIdOrderByDateDesc(businessId)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    public MouvementResponse addMouvement(Long businessId, MouvementRequest request) {
        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new RuntimeException("Business not found"));

        Mouvement mouvement = new Mouvement();
        mouvement.setBusiness(business);
        mouvement.setLibelle(request.getLibelle());
        mouvement.setType(request.getType());
        mouvement.setCompte(request.getCompte());
        mouvement.setMontant(request.getMontant());
        mouvement.setCategorie(request.getCategorie());
        mouvement.setDate(request.getDate() != null ? request.getDate() : LocalDate.now());
        mouvement.setStatut(request.getStatut() != null ? request.getStatut() : "en_attente");

        return toDto(mouvementRepository.save(mouvement));
    }

    public MouvementResponse updateMouvementStatus(Long id, String statut, String compte, String categorie) {
        Mouvement m = mouvementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mouvement not found"));

        if (statut != null)
            m.setStatut(statut);
        if (compte != null)
            m.setCompte(compte);
        if (categorie != null)
            m.setCategorie(categorie);

        return toDto(mouvementRepository.save(m));
    }
}
