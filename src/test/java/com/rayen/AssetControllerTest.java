package com.rayen;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rayen.financialMarketManagement.entity.Asset;
import com.rayen.financialMarketManagement.entity.AssetType;
import com.rayen.financialMarketManagement.service.AssetService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AssetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AssetService assetService;

    // ----------------------------------------------------------------
    // POST /api/v1/assets
    // ----------------------------------------------------------------

    @Test
    void createAsset_validRequest_shouldReturn201() throws Exception {
        Asset asset = new Asset();
        when(assetService.createAsset(any(Asset.class))).thenReturn(asset);

        mockMvc.perform(post("/api/v1/assets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(asset)))
                .andExpect(status().isCreated());
    }

    // ----------------------------------------------------------------
    // GET /api/v1/assets/{id}
    // ----------------------------------------------------------------

    @Test
    void getAssetById_exists_shouldReturn200() throws Exception {
        Asset asset = new Asset();
        when(assetService.getAssetById(1)).thenReturn(asset);

        mockMvc.perform(get("/api/v1/assets/1"))
                .andExpect(status().isOk());
    }

    // ----------------------------------------------------------------
    // GET /api/v1/assets
    // ----------------------------------------------------------------

    @Test
    void getAllAssets_shouldReturn200WithList() throws Exception {
        when(assetService.getAllAssets()).thenReturn(List.of(new Asset(), new Asset()));

        mockMvc.perform(get("/api/v1/assets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getAllAssets_empty_shouldReturn200WithEmptyList() throws Exception {
        when(assetService.getAllAssets()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/assets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // ----------------------------------------------------------------
    // GET /api/v1/assets/owner/{ownerId}
    // ----------------------------------------------------------------

    @Test
    void getAssetsByOwner_shouldReturn200WithList() throws Exception {
        when(assetService.getAssetsByOwnerId("owner123")).thenReturn(List.of(new Asset()));

        mockMvc.perform(get("/api/v1/assets/owner/owner123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getAssetsByOwner_noAssets_shouldReturn200WithEmptyList() throws Exception {
        when(assetService.getAssetsByOwnerId("unknown")).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/assets/owner/unknown"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // ----------------------------------------------------------------
    // GET /api/v1/assets/type/{assetType}
    // ----------------------------------------------------------------



    // ----------------------------------------------------------------
    // PUT /api/v1/assets/{id}
    // ----------------------------------------------------------------

    @Test
    void updateAsset_exists_shouldReturn200() throws Exception {
        Asset asset = new Asset();
        when(assetService.updateAsset(eq(1), any(Asset.class))).thenReturn(asset);

        mockMvc.perform(put("/api/v1/assets/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(asset)))
                .andExpect(status().isOk());
    }

    // ----------------------------------------------------------------
    // DELETE /api/v1/assets/{id}
    // ----------------------------------------------------------------

    @Test
    void deleteAsset_exists_shouldReturn204() throws Exception {
        doNothing().when(assetService).deleteAsset(1);

        mockMvc.perform(delete("/api/v1/assets/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteAsset_notFound_shouldReturn404() throws Exception {
        doThrow(new IllegalArgumentException("Asset not found"))
                .when(assetService).deleteAsset(99);

        mockMvc.perform(delete("/api/v1/assets/99"))
                .andExpect(status().isNotFound());
    }
}