package com.rayen.blockChainManagement.model;

import lombok.Data;

@Data
public class ChatRequest {
    private String sessionId;
    private String message;
}
