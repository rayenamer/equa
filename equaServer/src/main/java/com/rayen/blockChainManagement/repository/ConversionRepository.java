package com.rayen.blockChainManagement.repository;


import com.rayen.blockChainManagement.entity.Conversion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;

@Repository
public interface ConversionRepository extends JpaRepository<Conversion, Long> {

    @Query("SELECT COALESCE(SUM(c.dinarAmount), 0) FROM Conversion c")
    BigDecimal sumAllDinarsConverted();
}
