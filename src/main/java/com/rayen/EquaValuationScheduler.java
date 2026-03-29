package com.rayen;

import com.rayen.blockChainManagement.service.EquaValuationEngine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
@Slf4j
public class EquaValuationScheduler {
    private final EquaValuationEngine equaValuationEngine;

    // Runs every 1 seconds
    @Scheduled(fixedRate = 1_000)
    public void refreshRate() {
        //log.info("[Scheduler] Refreshing EQUA/Dinar rate...");
        BigDecimal rate = equaValuationEngine.equaEngine();
        //log.info("[Scheduler] Current EQUA/Dinar rate: {} Dinar", rate);
    }
}
