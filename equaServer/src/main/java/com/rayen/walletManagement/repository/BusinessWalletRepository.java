package com.rayen.walletManagement.repository;

import com.rayen.walletManagement.entity.BusinessWallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BusinessWalletRepository extends JpaRepository<BusinessWallet, Long> {
    Optional<BusinessWallet> findByBusinessId(Long businessId);
}
