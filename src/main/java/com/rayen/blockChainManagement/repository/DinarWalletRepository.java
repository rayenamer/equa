package com.rayen.blockChainManagement.repository;


import com.rayen.blockChainManagement.entity.DinarWallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DinarWalletRepository extends JpaRepository<DinarWallet, String> {
    Optional<DinarWallet> findByUserId(String userId);
    List<DinarWallet> findAllByUserId(String userId);
    boolean existsByUserId(String userId);
}
