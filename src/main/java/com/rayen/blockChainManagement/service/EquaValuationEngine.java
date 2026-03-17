package com.rayen.blockChainManagement.service;

import com.rayen.blockChainManagement.repository.DinarRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
@Slf4j
public class EquaValuationEngine {

    private final DinarRepository dinarRepository;
    //private final EquaRepository equaRepository;

    //prevent lower than 1 valuation
    private static final BigDecimal PRICE_FLOOR = new BigDecimal("1.000");

    //8 after el ,
    private static final int SCALE = 8;
    private static final RoundingMode ROUNDING = RoundingMode.HALF_UP;

    // Always holds the latest computed rate, initialized to 1.00000000
    private BigDecimal currentRate = BigDecimal.ONE.setScale(SCALE, ROUNDING);

    /**
     * Core valuation engine.
     * Formula: EQUA/Dinar rate = Total Dinars in System / Total EQUAs in System
     *
     * Interpretation:
     *  - If more Dinars back fewer EQUAs → each EQUA is worth more Dinars (appreciation)
     *  - If more EQUAs are minted with same Dinar backing → each EQUA is worth fewer Dinars (dilution)
     *
     * @return BigDecimal — the current value of 1 EQUA expressed in Dinars
     */
    @Transactional(readOnly = true)
    public BigDecimal equaEngine() {
        long totalDinars = dinarRepository.countDinarsInSystem();
        //long totalEquas = equaRepository.countEquasInSystem();

        log.info("[EquaValuationEngine] Total Dinars in system : {}", totalDinars);
        //log.info("[EquaValuationEngine] Total EQUAs in system  : {}", totalEquas);

        // Guard: if no Dinars back the system, return price floor
        if (totalDinars == 0) {
            log.warn("[EquaValuationEngine] No Dinar reserves found — returning price floor: {}", PRICE_FLOOR);
            currentRate = PRICE_FLOOR;   // ← keep currentRate in sync
            return currentRate;
        }

        //BigDecimal dinarSupply = new BigDecimal(totalDinars);
        //BigDecimal equaSupply  = new BigDecimal(totalEquas);

        // Rate = Dinar reserves / EQUA supply
        //BigDecimal rate = dinarSupply.divide(equaSupply, SCALE, ROUNDING);

        // Enforce price floor
        //if (rate.compareTo(PRICE_FLOOR) < 0) {
        //    log.warn("[EquaValuationEngine] Computed rate {} is below floor — clamping to {}", rate, PRICE_FLOOR);
        //    currentRate = PRICE_FLOOR;
        //    return currentRate;
        //}

        //log.info("[EquaValuationEngine] 1 EQUA = {} Dinar", rate);
        //currentRate = rate;

        // Temporary: returning 1.00000000 until EQUA supply is wired in
        currentRate = BigDecimal.ONE.setScale(SCALE, ROUNDING);
        log.info("[EquaValuationEngine] Current EQUA/Dinar rate: {}", currentRate);
        return currentRate;
    }

    // Any service can call this without triggering a DB hit
    public BigDecimal getCurrentRate() {
        return currentRate;
    }
}