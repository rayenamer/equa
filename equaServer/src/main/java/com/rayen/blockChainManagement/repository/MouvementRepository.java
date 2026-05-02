package com.rayen.blockChainManagement.repository;

import com.rayen.blockChainManagement.entity.Mouvement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MouvementRepository extends JpaRepository<Mouvement, Long> {
    List<Mouvement> findByBusinessIdOrderByDateDesc(Long businessId);
}
