package com.rayen.forumManagement.controller;

import com.rayen.forumManagement.model.*;
import com.rayen.forumManagement.service.ForumService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    @Operation(summary = "Create a new topic (any user)")
    public ResponseEntity<ForumTopicDTO> createTopic(@RequestBody CreateForumTopicRequest request) {
        ForumTopicDTO created = forumService.createTopic(request);
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
    @Operation(summary = "Add a message to a topic (any user)")
    public ResponseEntity<ForumMessageDTO> addMessage(
            @PathVariable Long id,
            @RequestBody CreateForumMessageRequest request) {
        ForumMessageDTO message = forumService.addMessage(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(message);
    }

    @GetMapping("/topics/{id}/messages")
    @Operation(summary = "Get all messages of a topic")
    public List<ForumMessageDTO> getMessages(@PathVariable Long id) {
        return forumService.getMessages(id);
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
