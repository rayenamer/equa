package com.rayen.userManaement.controller;

import com.rayen.userManaement.model.AuditLogDTO;
import com.rayen.userManaement.model.CreateUserRequest;
import com.rayen.userManaement.model.UserDTO;
import com.rayen.userManaement.model.UserRolesDTO;
import com.rayen.userManaement.service.UserService;
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
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "User registration, authentication, and audit")
public class UserController {

    private final UserService userService;

    @GetMapping
    @Operation(summary = "Get all users")
    public List<UserDTO> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID")
    public UserDTO getUserById(@PathVariable Long id) {
        return userService.getById(id);
    }

    @PostMapping
    @Operation(summary = "Create a new user (ASSISTANT or OBSERVER)")
    public ResponseEntity<UserDTO> createUser(@RequestBody CreateUserRequest request) {
        UserDTO created = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user")
    public UserDTO updateUser(@PathVariable Long id, @RequestBody UserDTO dto) {
        return userService.updateUser(id, dto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    // --- Audit log ---

    @PostMapping("/observers/{observerUserId}/audit-log")
    @Operation(summary = "Log an action (ObserverUser only)")
    public ResponseEntity<AuditLogDTO> logAction(
            @PathVariable Long observerUserId,
            @RequestBody Map<String, Object> body) {
        String action = (String) body.get("action");
        Integer eventId = body.get("eventId") != null ? ((Number) body.get("eventId")).intValue() : null;
        AuditLogDTO log = userService.logAction(observerUserId, action != null ? action : "", eventId);
        return ResponseEntity.status(HttpStatus.CREATED).body(log);
    }

    @GetMapping("/observers/{observerUserId}/audit-logs")
    @Operation(summary = "Get audit logs by observer")
    public List<AuditLogDTO> getAuditLogsByObserver(@PathVariable Long observerUserId) {
        return userService.getAuditLogsByObserver(observerUserId);
    }

    @GetMapping("/audit-logs")
    @Operation(summary = "Get all audit logs")
    public List<AuditLogDTO> getAllAuditLogs() {
        return userService.getAllAuditLogs();
    }

    // --- Roles management ---

    @GetMapping("/{id}/roles")
    @Operation(summary = "Get user roles and permissions")
    public UserRolesDTO getUserRoles(@PathVariable Long id) {
        return userService.getUserRoles(id);
    }

    @PutMapping("/{id}/roles")
    @Operation(summary = "Update user roles and permissions")
    public UserRolesDTO updateUserRoles(@PathVariable Long id, @RequestBody UserRolesDTO rolesDTO) {
        return userService.updateUserRoles(id, rolesDTO);
    }

    // --- Exception Handler ---

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<HashMap<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        HashMap<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now().toString());
        error.put("status", 400);
        error.put("error", "Bad Request");
        error.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}
