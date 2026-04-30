package com.rayen.blockChainManagement.repository;

import com.rayen.blockChainManagement.entity.CharginCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CharginCardRepository extends JpaRepository<CharginCard,Integer> {

    CharginCard findByCardCode(String cardCode);
}
