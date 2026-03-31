package com.rayen;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rayen.blockChainManagement.model.NodeRequest;
import com.rayen.blockChainManagement.model.NodeResponse;
import com.rayen.blockChainManagement.service.NodeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class NodeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private NodeService nodeService;

    // ----------------------------------------------------------------
    // POST /api/v1/nodes
    // ----------------------------------------------------------------

    @Test
    void createNode_validRequest_shouldReturn201() throws Exception {
        NodeRequest request = new NodeRequest();
        NodeResponse response = new NodeResponse();

        when(nodeService.createNode(any(NodeRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/nodes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void createNode_serviceThrows_shouldReturn400() throws Exception {
        when(nodeService.createNode(any())).thenThrow(new IllegalArgumentException("Invalid node"));

        mockMvc.perform(post("/api/v1/nodes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new NodeRequest())))
                .andExpect(status().isBadRequest());
    }

    // ----------------------------------------------------------------
    // GET /api/v1/nodes/{id}
    // ----------------------------------------------------------------

    @Test
    void getNodeById_exists_shouldReturn200() throws Exception {
        NodeResponse response = new NodeResponse();
        when(nodeService.getNodeById(1)).thenReturn(Optional.of(response));

        mockMvc.perform(get("/api/v1/nodes/1"))
                .andExpect(status().isOk());
    }

    @Test
    void getNodeById_notFound_shouldReturn404() throws Exception {
        when(nodeService.getNodeById(99)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/nodes/99"))
                .andExpect(status().isNotFound());
    }

    // ----------------------------------------------------------------
    // GET /api/v1/nodes
    // ----------------------------------------------------------------

    @Test
    void getAllNodes_shouldReturn200WithList() throws Exception {
        when(nodeService.getAllNodes()).thenReturn(List.of(new NodeResponse(), new NodeResponse()));

        mockMvc.perform(get("/api/v1/nodes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getAllNodes_empty_shouldReturn200WithEmptyList() throws Exception {
        when(nodeService.getAllNodes()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/nodes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // ----------------------------------------------------------------
    // GET /api/v1/nodes/search
    // ----------------------------------------------------------------

    @Test
    void searchNodes_noParams_shouldReturn200() throws Exception {
        when(nodeService.getNodesByOptionalParams(any(), any(), any(), any(), any(),
                any(), any(), any(), any(), any()))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/v1/nodes/search"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void searchNodes_withParams_shouldReturn200() throws Exception {
        when(nodeService.getNodesByOptionalParams(any(), any(), any(), any(), any(),
                any(), any(), any(), any(), any()))
                .thenReturn(List.of(new NodeResponse()));

        mockMvc.perform(get("/api/v1/nodes/search")
                        .param("status", "ONLINE")
                        .param("minReputationScore", "10.0")
                        .param("maxReputationScore", "100.0")
                        .param("nodeType", "VALIDATOR")
                        .param("location", "TN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    // ----------------------------------------------------------------
    // GET /api/v1/nodes/online
    // ----------------------------------------------------------------

    @Test
    void getOnlineNodes_shouldReturn200() throws Exception {
        when(nodeService.getOnlineNodes()).thenReturn(List.of(new NodeResponse()));

        mockMvc.perform(get("/api/v1/nodes/online"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    // ----------------------------------------------------------------
    // GET /api/v1/nodes/top-reputation
    // ----------------------------------------------------------------

    @Test
    void getTopNodesByReputation_shouldReturn200() throws Exception {
        when(nodeService.getTopNodesByReputation()).thenReturn(List.of(new NodeResponse()));

        mockMvc.perform(get("/api/v1/nodes/top-reputation"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    // ----------------------------------------------------------------
    // PATCH /api/v1/nodes/{id}/status
    // ----------------------------------------------------------------

    @Test
    void updateNodeStatus_exists_shouldReturn200() throws Exception {
        when(nodeService.updateNodeStatus(1, "ONLINE")).thenReturn(new NodeResponse());

        mockMvc.perform(patch("/api/v1/nodes/1/status")
                        .param("status", "ONLINE"))
                .andExpect(status().isOk());
    }

    @Test
    void updateNodeStatus_notFound_shouldReturn404() throws Exception {
        when(nodeService.updateNodeStatus(99, "ONLINE"))
                .thenThrow(new IllegalArgumentException("Node not found"));

        mockMvc.perform(patch("/api/v1/nodes/99/status")
                        .param("status", "ONLINE"))
                .andExpect(status().isNotFound());
    }

    // ----------------------------------------------------------------
    // PATCH /api/v1/nodes/{id}/reputation
    // ----------------------------------------------------------------

    @Test
    void updateReputationScore_exists_shouldReturn200() throws Exception {
        when(nodeService.updateReputationScore(1, 95.0)).thenReturn(new NodeResponse());

        mockMvc.perform(patch("/api/v1/nodes/1/reputation")
                        .param("score", "95.0"))
                .andExpect(status().isOk());
    }

    @Test
    void updateReputationScore_notFound_shouldReturn404() throws Exception {
        when(nodeService.updateReputationScore(99, 95.0))
                .thenThrow(new IllegalArgumentException("Node not found"));

        mockMvc.perform(patch("/api/v1/nodes/99/reputation")
                        .param("score", "95.0"))
                .andExpect(status().isNotFound());
    }

    // ----------------------------------------------------------------
    // PATCH /api/v1/nodes/{id}/heartbeat
    // ----------------------------------------------------------------

    @Test
    void updateLastSeen_exists_shouldReturn200() throws Exception {
        when(nodeService.updateLastSeen(1)).thenReturn(new NodeResponse());

        mockMvc.perform(patch("/api/v1/nodes/1/heartbeat"))
                .andExpect(status().isOk());
    }

    @Test
    void updateLastSeen_notFound_shouldReturn404() throws Exception {
        when(nodeService.updateLastSeen(99))
                .thenThrow(new IllegalArgumentException("Node not found"));

        mockMvc.perform(patch("/api/v1/nodes/99/heartbeat"))
                .andExpect(status().isNotFound());
    }

    // ----------------------------------------------------------------
    // GET /api/v1/nodes/count/status/{status}
    // ----------------------------------------------------------------

    @Test
    void countNodesByStatus_shouldReturn200WithCount() throws Exception {
        when(nodeService.countNodesByStatus("ONLINE")).thenReturn(7L);

        mockMvc.perform(get("/api/v1/nodes/count/status/ONLINE"))
                .andExpect(status().isOk())
                .andExpect(content().string("7"));
    }

    @Test
    void countNodesByStatus_noneFound_shouldReturn0() throws Exception {
        when(nodeService.countNodesByStatus("OFFLINE")).thenReturn(0L);

        mockMvc.perform(get("/api/v1/nodes/count/status/OFFLINE"))
                .andExpect(status().isOk())
                .andExpect(content().string("0"));
    }

    // ----------------------------------------------------------------
    // DELETE /api/v1/nodes/{id}
    // ----------------------------------------------------------------

    @Test
    void deleteNode_exists_shouldReturn204() throws Exception {
        doNothing().when(nodeService).deleteNode(1);

        mockMvc.perform(delete("/api/v1/nodes/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteNode_notFound_shouldReturn404() throws Exception {
        doThrow(new IllegalArgumentException("Node not found"))
                .when(nodeService).deleteNode(99);

        mockMvc.perform(delete("/api/v1/nodes/99"))
                .andExpect(status().isNotFound());
    }

    // ----------------------------------------------------------------
    // GET /api/v1/nodes/exists/ip/{ipAddress}
    // ----------------------------------------------------------------

    @Test
    void existsByIpAddress_exists_shouldReturnTrue() throws Exception {
        when(nodeService.existsByIpAddress("192.168.1.1")).thenReturn(true);

        mockMvc.perform(get("/api/v1/nodes/exists/ip/192.168.1.1"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void existsByIpAddress_notExists_shouldReturnFalse() throws Exception {
        when(nodeService.existsByIpAddress("10.0.0.1")).thenReturn(false);

        mockMvc.perform(get("/api/v1/nodes/exists/ip/10.0.0.1"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    // ----------------------------------------------------------------
    // GET /api/v1/nodes/exists/public-key/{publicKey}
    // ----------------------------------------------------------------

    @Test
    void existsByPublicKey_exists_shouldReturnTrue() throws Exception {
        when(nodeService.existsByPublicKey("myPublicKey123")).thenReturn(true);

        mockMvc.perform(get("/api/v1/nodes/exists/public-key/myPublicKey123"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void existsByPublicKey_notExists_shouldReturnFalse() throws Exception {
        when(nodeService.existsByPublicKey("unknownKey")).thenReturn(false);

        mockMvc.perform(get("/api/v1/nodes/exists/public-key/unknownKey"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }
}