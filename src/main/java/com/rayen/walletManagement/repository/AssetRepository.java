package com.rayen.walletManagement.repository;

import com.rayen.walletManagement.entity.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("walletAssetRepository")
public interface AssetRepository extends JpaRepository<Asset, Long> {

    List<Asset> findByOwnerId(String ownerId);

    List<Asset> findByAssetType(String assetType);

    List<Asset> findByWalletWalletId(Long walletId);

    List<Asset> findByStatus(String status);

    @Query("SELECT SUM(a.value) FROM WalletAsset a WHERE a.ownerId = :ownerId")
    Double getTotalAssetValueByOwnerId(@Param("ownerId") String ownerId);

    @Query("SELECT SUM(a.value) FROM WalletAsset a WHERE a.wallet.walletId = :walletId")
    Double getTotalAssetValueByWalletId(@Param("walletId") Long walletId);
}
