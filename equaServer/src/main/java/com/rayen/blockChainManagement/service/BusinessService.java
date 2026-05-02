package com.rayen.blockChainManagement.service;

import com.rayen.blockChainManagement.dto.BusinessResponse;
import com.rayen.blockChainManagement.entity.Business;
import com.rayen.blockChainManagement.repository.BusinessRepository;
import com.rayen.userManaement.entity.User;
import com.rayen.userManaement.repository.UserRepository;
import com.rayen.walletManagement.entity.BusinessWallet;
import com.rayen.walletManagement.repository.BusinessWalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BusinessService {

    private final BusinessRepository businessRepository;
    private final BusinessWalletRepository businessWalletRepository;
    private final UserRepository userRepository;

    @Autowired
    public BusinessService(BusinessRepository businessRepository,
            BusinessWalletRepository businessWalletRepository,
            UserRepository userRepository) {
        this.businessRepository = businessRepository;
        this.businessWalletRepository = businessWalletRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Business createBusiness(Long userId, Business businessDetails) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (businessDetails.getName() == null || businessDetails.getName().isEmpty()) {
            throw new IllegalArgumentException("Business name is required");
        }

        // Create Business
        businessDetails.setOwner(owner);
        Business savedBusiness = businessRepository.save(businessDetails);

        // Create BusinessWallet
        BusinessWallet wallet = new BusinessWallet();
        wallet.setBusiness(savedBusiness);
        wallet.setStatus("ACTIVE");
        wallet.setEquaAmount(0f);
        wallet.setLastActivityAt(LocalDateTime.now());

        businessWalletRepository.save(wallet);

        return savedBusiness;
    }

    public List<BusinessResponse> getBusinessesByUser(Long userId) {
        return businessRepository.findByOwnerId(userId).stream()
                .map(b -> {
                    BusinessResponse dto = new BusinessResponse();
                    dto.setId(b.getId());
                    dto.setName(b.getName());
                    dto.setIndustry(b.getIndustry());
                    dto.setRegistrationNumber(b.getRegistrationNumber());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public Optional<Business> getBusinessById(Long businessId) {
        return businessRepository.findById(businessId);
    }
}
