package com.rayen.blockChainManagement.service;

import com.rayen.blockChainManagement.repository.ConversionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
@Slf4j
public class EquaValuationEngine {

    private final ConversionRepository conversionRepository;
    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    private static final BigDecimal PRICE_FLOOR = new BigDecimal("1.00000000");
    private static final BigDecimal PRICE_CEILING = new BigDecimal("1000.00000000");
    private static final BigDecimal SENSITIVITY = new BigDecimal("10000");
    private static final BigDecimal JITTER_AMPLITUDE = new BigDecimal("0.00001");
    private static final int SCALE = 8;
    private static final RoundingMode ROUNDING = RoundingMode.HALF_UP;
    private static final MathContext MC = new MathContext(18, ROUNDING);

    private final AtomicReference<BigDecimal> currentRate = new AtomicReference<>(
            BigDecimal.ONE.setScale(SCALE, ROUNDING));

    public void registerEmitter(SseEmitter emitter) {
        emitters.add(emitter);
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError((e) -> emitters.remove(emitter));
    }

    @Transactional(readOnly = true)
    public BigDecimal computeAndBroadcast() {
        BigDecimal rate = equaEngine();
        emitters.forEach(emitter -> {
            try {
                emitter.send(rate);
            } catch (IOException e) {
                emitters.remove(emitter);
            }
        });
        return rate;
    }

    @Transactional(readOnly = true)
    public BigDecimal equaEngine() {
        BigDecimal totalConverted = conversionRepository.sumAllDinarsConverted();

        if (totalConverted == null || totalConverted.compareTo(BigDecimal.ZERO) <= 0) {
            log.info("[ValuationEngine] No conversions yet — returning base rate: {}", PRICE_FLOOR);
            currentRate.set(PRICE_FLOOR);
            return PRICE_FLOOR;
        }

        log.info("[ValuationEngine] Total Dinars ever converted: {}", totalConverted);

        BigDecimal adoptionBonus = totalConverted.divide(SENSITIVITY, SCALE, ROUNDING);
        BigDecimal rawRate = PRICE_FLOOR.add(adoptionBonus).setScale(SCALE, ROUNDING);
        BigDecimal result = clamp(applyJitter(rawRate));

        currentRate.set(result);
        log.info("[ValuationEngine] adoptionBonus={} raw={} final={}", adoptionBonus, rawRate, result);
        return result;
    }

    public BigDecimal getCurrentRate() {
        return currentRate.get();
    }

    private BigDecimal applyJitter(BigDecimal rate) {
        double raw = (Math.random() - 0.5) * 2;
        return rate.add(JITTER_AMPLITUDE.multiply(BigDecimal.valueOf(raw), MC))
                .setScale(SCALE, ROUNDING);
    }

    private BigDecimal clamp(BigDecimal rate) {
        if (rate.compareTo(PRICE_FLOOR) < 0)
            return PRICE_FLOOR;
        if (rate.compareTo(PRICE_CEILING) > 0)
            return PRICE_CEILING;
        return rate;
    }
}