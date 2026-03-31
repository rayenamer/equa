package com.rayen;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rayen.forumManagement.model.*;
import com.rayen.forumManagement.service.ForumService;
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
class ForumControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ForumService forumService;

    // ----------------------------------------------------------------
    // GET /api/forum/topics
    // ----------------------------------------------------------------

    @Test
    void getAllTopics_shouldReturn200WithList() throws Exception {
        when(forumService.getAllTopics()).thenReturn(List.of(new ForumTopicDTO(), new ForumTopicDTO()));

        mockMvc.perform(get("/api/forum/topics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getAllTopics_empty_shouldReturn200WithEmptyList() throws Exception {
        when(forumService.getAllTopics()).thenReturn(List.of());

        mockMvc.perform(get("/api/forum/topics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // ----------------------------------------------------------------
    // GET /api/forum/topics/{id}
    // ----------------------------------------------------------------

    @Test
    void getTopicById_exists_shouldReturn200() throws Exception {
        when(forumService.getTopicById(1L)).thenReturn(new ForumTopicDTO());

        mockMvc.perform(get("/api/forum/topics/1"))
                .andExpect(status().isOk());
    }

    @Test
    void getTopicById_notFound_shouldReturn400() throws Exception {
        when(forumService.getTopicById(99L))
                .thenThrow(new IllegalArgumentException("Topic not found"));

        mockMvc.perform(get("/api/forum/topics/99"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Topic not found"))
                .andExpect(jsonPath("$.status").value(400));
    }

    // ----------------------------------------------------------------
    // GET /api/forum/topics/by-user/{userId}
    // ----------------------------------------------------------------

    @Test
    void getTopicsByUser_shouldReturn200WithList() throws Exception {
        when(forumService.getTopicsByUser(1L)).thenReturn(List.of(new ForumTopicDTO()));

        mockMvc.perform(get("/api/forum/topics/by-user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getTopicsByUser_noTopics_shouldReturn200WithEmptyList() throws Exception {
        when(forumService.getTopicsByUser(99L)).thenReturn(List.of());

        mockMvc.perform(get("/api/forum/topics/by-user/99"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // ----------------------------------------------------------------
    // POST /api/forum/topics
    // ----------------------------------------------------------------

    @Test
    void createTopic_authenticated_shouldReturn201() throws Exception {
        CreateForumTopicRequest request = new CreateForumTopicRequest();
        when(forumService.createTopic(any(CreateForumTopicRequest.class), any()))
                .thenReturn(new ForumTopicDTO());

        mockMvc.perform(post("/api/forum/topics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void createTopic_unauthenticated_shouldStillReturn201() throws Exception {
        CreateForumTopicRequest request = new CreateForumTopicRequest();
        when(forumService.createTopic(any(CreateForumTopicRequest.class), isNull()))
                .thenReturn(new ForumTopicDTO());

        mockMvc.perform(post("/api/forum/topics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    // ----------------------------------------------------------------
    // PUT /api/forum/topics/{id}
    // ----------------------------------------------------------------

    @Test
    void updateTopic_exists_shouldReturn200() throws Exception {
        UpdateForumTopicRequest request = new UpdateForumTopicRequest();
        when(forumService.updateTopic(eq(1L), any(UpdateForumTopicRequest.class)))
                .thenReturn(new ForumTopicDTO());

        mockMvc.perform(put("/api/forum/topics/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void updateTopic_notFound_shouldReturn400() throws Exception {
        when(forumService.updateTopic(eq(99L), any()))
                .thenThrow(new IllegalArgumentException("Topic not found"));

        mockMvc.perform(put("/api/forum/topics/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UpdateForumTopicRequest())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Topic not found"));
    }

    // ----------------------------------------------------------------
    // DELETE /api/forum/topics/{id}
    // ----------------------------------------------------------------

    @Test
    void deleteTopic_exists_shouldReturn204() throws Exception {
        doNothing().when(forumService).deleteTopic(1L);

        mockMvc.perform(delete("/api/forum/topics/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteTopic_notFound_shouldReturn400() throws Exception {
        doThrow(new IllegalArgumentException("Topic not found"))
                .when(forumService).deleteTopic(99L);

        mockMvc.perform(delete("/api/forum/topics/99"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Topic not found"));
    }

    // ----------------------------------------------------------------
    // POST /api/forum/topics/{id}/messages
    // ----------------------------------------------------------------

    @Test
    void addMessage_shouldReturn201() throws Exception {
        CreateForumMessageRequest request = new CreateForumMessageRequest();
        when(forumService.addMessage(eq(1L), any(CreateForumMessageRequest.class), any()))
                .thenReturn(new ForumMessageDTO());

        mockMvc.perform(post("/api/forum/topics/1/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void addMessage_topicNotFound_shouldReturn400() throws Exception {
        when(forumService.addMessage(eq(99L), any(), any()))
                .thenThrow(new IllegalArgumentException("Topic not found"));

        mockMvc.perform(post("/api/forum/topics/99/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CreateForumMessageRequest())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Topic not found"));
    }

    // ----------------------------------------------------------------
    // GET /api/forum/topics/{id}/messages
    // ----------------------------------------------------------------

    @Test
    void getMessages_shouldReturn200WithList() throws Exception {
        when(forumService.getMessages(1L)).thenReturn(List.of(new ForumMessageDTO()));

        mockMvc.perform(get("/api/forum/topics/1/messages"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getMessages_empty_shouldReturn200WithEmptyList() throws Exception {
        when(forumService.getMessages(1L)).thenReturn(List.of());

        mockMvc.perform(get("/api/forum/topics/1/messages"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // ----------------------------------------------------------------
    // PUT /api/forum/messages/{id}
    // ----------------------------------------------------------------

    @Test
    void updateMessage_exists_shouldReturn200() throws Exception {
        UpdateForumMessageRequest request = new UpdateForumMessageRequest();
        when(forumService.updateMessage(eq(1L), any(UpdateForumMessageRequest.class)))
                .thenReturn(new ForumMessageDTO());

        mockMvc.perform(put("/api/forum/messages/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void updateMessage_notFound_shouldReturn400() throws Exception {
        when(forumService.updateMessage(eq(99L), any()))
                .thenThrow(new IllegalArgumentException("Message not found"));

        mockMvc.perform(put("/api/forum/messages/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UpdateForumMessageRequest())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Message not found"));
    }

    // ----------------------------------------------------------------
    // DELETE /api/forum/messages/{id}
    // ----------------------------------------------------------------

    @Test
    void deleteMessage_exists_shouldReturn204() throws Exception {
        doNothing().when(forumService).deleteMessage(1L);

        mockMvc.perform(delete("/api/forum/messages/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteMessage_notFound_shouldReturn400() throws Exception {
        doThrow(new IllegalArgumentException("Message not found"))
                .when(forumService).deleteMessage(99L);

        mockMvc.perform(delete("/api/forum/messages/99"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Message not found"));
    }

    // ----------------------------------------------------------------
    // POST /api/forum/topics/{id}/reports
    // ----------------------------------------------------------------

    @Test
    void reportTopic_shouldReturn201() throws Exception {
        CreateForumReportRequest request = new CreateForumReportRequest();
        when(forumService.reportTopic(eq(1L), any(CreateForumReportRequest.class), any()))
                .thenReturn(new ForumReportDTO());

        mockMvc.perform(post("/api/forum/topics/1/reports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void reportTopic_notFound_shouldReturn400() throws Exception {
        when(forumService.reportTopic(eq(99L), any(), any()))
                .thenThrow(new IllegalArgumentException("Topic not found"));

        mockMvc.perform(post("/api/forum/topics/99/reports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CreateForumReportRequest())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Topic not found"));
    }

    // ----------------------------------------------------------------
    // POST /api/forum/messages/{id}/reports
    // ----------------------------------------------------------------

    @Test
    void reportMessage_shouldReturn201() throws Exception {
        CreateForumReportRequest request = new CreateForumReportRequest();
        when(forumService.reportMessage(eq(1L), any(CreateForumReportRequest.class), any()))
                .thenReturn(new ForumReportDTO());

        mockMvc.perform(post("/api/forum/messages/1/reports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void reportMessage_notFound_shouldReturn400() throws Exception {
        when(forumService.reportMessage(eq(99L), any(), any()))
                .thenThrow(new IllegalArgumentException("Message not found"));

        mockMvc.perform(post("/api/forum/messages/99/reports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CreateForumReportRequest())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Message not found"));
    }

    // ----------------------------------------------------------------
    // GET /api/forum/reports/pending
    // ----------------------------------------------------------------

    @Test
    void getPendingReports_shouldReturn200WithList() throws Exception {
        when(forumService.getPendingReports()).thenReturn(List.of(new ForumReportDTO()));

        mockMvc.perform(get("/api/forum/reports/pending"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getPendingReports_empty_shouldReturn200WithEmptyList() throws Exception {
        when(forumService.getPendingReports()).thenReturn(List.of());

        mockMvc.perform(get("/api/forum/reports/pending"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // ----------------------------------------------------------------
    // GET /api/forum/topics/{id}/reports
    // ----------------------------------------------------------------

    @Test
    void getReportsForTopic_shouldReturn200WithList() throws Exception {
        when(forumService.getReportsForTopic(1L)).thenReturn(List.of(new ForumReportDTO()));

        mockMvc.perform(get("/api/forum/topics/1/reports"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getReportsForTopic_notFound_shouldReturn400() throws Exception {
        when(forumService.getReportsForTopic(99L))
                .thenThrow(new IllegalArgumentException("Topic not found"));

        mockMvc.perform(get("/api/forum/topics/99/reports"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Topic not found"));
    }

    // ----------------------------------------------------------------
    // GET /api/forum/messages/{id}/reports
    // ----------------------------------------------------------------

    @Test
    void getReportsForMessage_shouldReturn200WithList() throws Exception {
        when(forumService.getReportsForMessage(1L)).thenReturn(List.of(new ForumReportDTO()));

        mockMvc.perform(get("/api/forum/messages/1/reports"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getReportsForMessage_notFound_shouldReturn400() throws Exception {
        when(forumService.getReportsForMessage(99L))
                .thenThrow(new IllegalArgumentException("Message not found"));

        mockMvc.perform(get("/api/forum/messages/99/reports"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Message not found"));
    }

    // ----------------------------------------------------------------
    // POST /api/forum/reports/{id}/moderate
    // ----------------------------------------------------------------

    @Test
    void moderateReport_shouldReturn200() throws Exception {
        ModerateForumReportRequest request = new ModerateForumReportRequest();
        when(forumService.moderateReport(eq(1L), any(ModerateForumReportRequest.class), any()))
                .thenReturn(new ForumReportDTO());

        mockMvc.perform(post("/api/forum/reports/1/moderate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void moderateReport_notFound_shouldReturn400() throws Exception {
        when(forumService.moderateReport(eq(99L), any(), any()))
                .thenThrow(new IllegalArgumentException("Report not found"));

        mockMvc.perform(post("/api/forum/reports/99/moderate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ModerateForumReportRequest())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Report not found"));
    }

    // ----------------------------------------------------------------
    // @ExceptionHandler — global error shape
    // ----------------------------------------------------------------

    @Test
    void exceptionHandler_shouldReturnCorrectErrorShape() throws Exception {
        when(forumService.getTopicById(99L))
                .thenThrow(new IllegalArgumentException("Topic not found"));

        mockMvc.perform(get("/api/forum/topics/99"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Topic not found"))
                .andExpect(jsonPath("$.timestamp").exists());
    }
}