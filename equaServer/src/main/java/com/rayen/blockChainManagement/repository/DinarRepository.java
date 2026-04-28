package com.rayen.blockChainManagement.repository;


import com.rayen.blockChainManagement.entity.Dinar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DinarRepository extends JpaRepository<Dinar, String> {
    List<Dinar> findAllByWallet_WalletId(String walletId);
    List<Dinar> findAllByStorageNode_NodeId(Integer nodeId);
    long countByWallet_WalletId(String walletId);

    @Query("SELECT COUNT(d) FROM Dinar d")
    int countDinarsInSystem();
}
