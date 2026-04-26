package com.rayen.blockChainManagement.controller;

import com.rayen.blockChainManagement.service.EquaValuationEngine;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/rates")
@RequiredArgsConstructor
public class RateController {

    private final EquaValuationEngine valuationEngine;

    @GetMapping(value = "/current", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BigDecimal> getCurrentRate() {
        return ResponseEntity.ok(valuationEngine.getCurrentRate());
    }

    @GetMapping(path = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamRates() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        valuationEngine.registerEmitter(emitter);
        return emitter;
    }
}