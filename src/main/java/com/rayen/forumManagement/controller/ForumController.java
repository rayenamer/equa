package com.rayen.forumManagement.controller;

import com.rayen.forumManagement.model.*;
import com.rayen.forumManagement.service.ForumService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/forum")
@RequiredArgsConstructor
@Tag(name = "Forum Management", description = "Topics and messages - all users can create topics and send messages")
public class ForumController {

    private final ForumService forumService;

    @GetMapping("/topics")
    @Operation(summary = "Get all forum topics")
    public List<ForumTopicDTO> getAllTopics() {
        return forumService.getAllTopics();
    }

    @GetMapping("/topics/{id}")
    @Operation(summary = "Get topic by ID (with messages)")
    public ForumTopicDTO getTopicById(@PathVariable Long id) {
        return forumService.getTopicById(id);
    }

    @GetMapping("/topics/by-user/{userId}")
    @Operation(summary = "Get topics created by a user")
    public List<ForumTopicDTO> getTopicsByUser(@PathVariable Long userId) {
        return forumService.getTopicsByUser(userId);
    }

    @PostMapping("/topics")
    @Operation(summary = "Create a new topic (any authenticated user)")
    public ResponseEntity<ForumTopicDTO> createTopic(
            @RequestBody CreateForumTopicRequest request,
            Authentication authentication) {
        String email = authentication != null ? (String) authentication.getPrincipal() : null;
        ForumTopicDTO created = forumService.createTopic(request, email);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/topics/{id}")
    @Operation(summary = "Update a topic (title, description)")
    public ForumTopicDTO updateTopic(@PathVariable Long id, @RequestBody UpdateForumTopicRequest request) {
        return forumService.updateTopic(id, request);
    }

    @DeleteMapping("/topics/{id}")
    @Operation(summary = "Delete a topic")
    public ResponseEntity<Void> deleteTopic(@PathVariable Long id) {
        forumService.deleteTopic(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/topics/{id}/messages")
    @Operation(summary = "Add a message to a topic (any authenticated user)")
    public ResponseEntity<ForumMessageDTO> addMessage(
            @PathVariable Long id,
            @RequestBody CreateForumMessageRequest request,
            Authentication authentication) {
        String email = authentication != null ? (String) authentication.getPrincipal() : null;
        ForumMessageDTO message = forumService.addMessage(id, request, email);
        return ResponseEntity.status(HttpStatus.CREATED).body(message);
    }

    @GetMapping("/topics/{id}/messages")
    @Operation(summary = "Get all messages of a topic")
    public List<ForumMessageDTO> getMessages(@PathVariable Long id) {
        return forumService.getMessages(id);
    }

    @PutMapping("/messages/{id}")
    @Operation(summary = "Update a message", description = "Met à jour le texte d'un message. Le filtrage de langage inapproprié est réappliqué.")
    public ForumMessageDTO updateMessage(
            @PathVariable Long id,
            @RequestBody UpdateForumMessageRequest request) {
        return forumService.updateMessage(id, request);
    }

    @DeleteMapping("/messages/{id}")
    @Operation(summary = "Delete a message")
    public ResponseEntity<Void> deleteMessage(@PathVariable Long id) {
        forumService.deleteMessage(id);
        return ResponseEntity.noContent().build();
    }

    // --- Reports & moderation ---

    @PostMapping("/topics/{id}/reports")
    @Operation(summary = "Report a topic", description = "Signaler un sujet inapproprié. Au-delà d'un certain nombre de signalements, le sujet est automatiquement masqué.")
    public ResponseEntity<ForumReportDTO> reportTopic(
            @PathVariable Long id,
            @RequestBody CreateForumReportRequest request,
            Authentication authentication) {
        String email = authentication != null ? (String) authentication.getPrincipal() : null;
        ForumReportDTO report = forumService.reportTopic(id, request, email);
        return ResponseEntity.status(HttpStatus.CREATED).body(report);
    }

    @PostMapping("/messages/{id}/reports")
    @Operation(summary = "Report a message", description = "Signaler un message inapproprié. Au-delà d'un certain nombre de signalements, le message est automatiquement masqué.")
    public ResponseEntity<ForumReportDTO> reportMessage(
            @PathVariable Long id,
            @RequestBody CreateForumReportRequest request,
            Authentication authentication) {
        String email = authentication != null ? (String) authentication.getPrincipal() : null;
        ForumReportDTO report = forumService.reportMessage(id, request, email);
        return ResponseEntity.status(HttpStatus.CREATED).body(report);
    }

    @GetMapping("/reports/pending")
    @Operation(summary = "Get all pending reports", description = "Liste des signalements en attente, pour la modération par les assistants.")
    public List<ForumReportDTO> getPendingReports() {
        return forumService.getPendingReports();
    }

    @GetMapping("/topics/{id}/reports")
    @Operation(summary = "Get all reports for a topic")
    public List<ForumReportDTO> getReportsForTopic(@PathVariable Long id) {
        return forumService.getReportsForTopic(id);
    }

    @GetMapping("/messages/{id}/reports")
    @Operation(summary = "Get all reports for a message")
    public List<ForumReportDTO> getReportsForMessage(@PathVariable Long id) {
        return forumService.getReportsForMessage(id);
    }

    @PostMapping("/reports/{id}/moderate")
    @Operation(summary = "Moderate a report", description = "Action de modération par un ASSISTANT. Action = HIDE (masquer sujet/message) ou IGNORE (rejeter).")
    public ForumReportDTO moderateReport(
            @PathVariable Long id,
            @RequestBody ModerateForumReportRequest request,
            Authentication authentication) {
        String email = authentication != null ? (String) authentication.getPrincipal() : null;
        return forumService.moderateReport(id, request, email);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now().toString());
        error.put("status", 400);
        error.put("error", "Bad Request");
        error.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}
