package com.rayen.blockChainManagement.service;

import com.rayen.blockChainManagement.entity.CharginCard;
import com.rayen.blockChainManagement.repository.CharginCardRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChargingCardService {

    private final CharginCardRepository charginCardRepository;

    public void createChargingCard(CharginCard charginCard){
        charginCardRepository.save(charginCard);
    }

    @Transactional
    public Integer chargeCard(String cardCode) {
        CharginCard card = charginCardRepository.findByCardCode(cardCode);
        if (card == null) {
            return 0;
        }
        else {
            card.setConsumed(true);
            return card.getDinarAmount();
        }
    }

    @Transactional
    public Boolean isCardUser(String cardCode) {
        return charginCardRepository.findByCardCode(cardCode).getConsumed();
    }

}
