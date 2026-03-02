package com.rayen.financialMarketManagement.repository;

import com.rayen.financialMarketManagement.entity.Asset;
import com.rayen.financialMarketManagement.entity.AssetType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Integer> {

    List<Asset> findByOwnerId(String ownerId);

    List<Asset> findByAssetType(AssetType assetType);

    List<Asset> findByOwnerIdAndAssetType(String ownerId, AssetType assetType);
}
