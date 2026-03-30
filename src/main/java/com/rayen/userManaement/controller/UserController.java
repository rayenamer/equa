package com.rayen.userManaement.controller;

import com.rayen.userManaement.model.AuditLogDTO;
import com.rayen.userManaement.model.CreateUserRequest;
import com.rayen.userManaement.model.KycProfileDTO;
import com.rayen.userManaement.model.UserDTO;
import com.rayen.userManaement.model.UserRolesDTO;
import com.rayen.userManaement.service.UserService;
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

    @GetMapping("/{id}/risk-level")
    @Operation(summary = "Get observer risk level")
    public Map<String, String> getObserverRiskLevel(@PathVariable Long id) {
        return Map.of("riskLevel", userService.getObserverRiskLevel(id));
    }

    @PutMapping("/{id}/risk-level")
    @Operation(summary = "Update observer risk level with remediation rules")
    public ResponseEntity<Map<String, String>> updateObserverRiskLevel(
            @PathVariable Long id,
            @RequestBody Map<String, String> body,
            Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Non authentifie."));
        }
        String actorEmail = (String) authentication.getPrincipal();
        String riskLevel = body.get("riskLevel");
        return ResponseEntity.ok(Map.of("riskLevel", userService.updateObserverRiskLevel(id, riskLevel, actorEmail)));
    }

    @GetMapping("/{id}/kyc-status")
    @Operation(summary = "Get observer KYC status")
    public Map<String, String> getObserverKycStatus(@PathVariable Long id) {
        return Map.of("kycStatus", userService.getObserverKycStatus(id));
    }

    @GetMapping("/me/kyc-profile")
    @Operation(summary = "Get own KYC profile (OBSERVER connecte)")
    public ResponseEntity<KycProfileDTO> getMyKycProfile(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String actorEmail = (String) authentication.getPrincipal();
        return ResponseEntity.ok(userService.getMyKycProfile(actorEmail));
    }

    @PostMapping("/me/kyc-profile")
    @Operation(summary = "Create own KYC profile — premiere soumission (OBSERVER connecte)")
    public ResponseEntity<KycProfileDTO> createMyKycProfile(
            @RequestBody KycProfileDTO request,
            Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String actorEmail = (String) authentication.getPrincipal();
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createMyKycProfile(request, actorEmail));
    }

    @PutMapping("/me/kyc-profile")
    @Operation(summary = "Update own KYC profile (OBSERVER connecte)")
    public ResponseEntity<KycProfileDTO> updateMyKycProfile(
            @RequestBody KycProfileDTO request,
            Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String actorEmail = (String) authentication.getPrincipal();
        return ResponseEntity.ok(userService.updateMyKycProfile(request, actorEmail));
    }

    @PutMapping("/{id}/kyc-status")
    @Operation(summary = "Update observer KYC status (ASSISTANT only)")
    public ResponseEntity<Map<String, String>> updateObserverKycStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body,
            Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Non authentifie."));
        }
        String actorEmail = (String) authentication.getPrincipal();
        String kycStatus = body.get("kycStatus");
        String note = body.get("note");
        return ResponseEntity.ok(Map.of("kycStatus", userService.updateObserverKycStatus(id, kycStatus, actorEmail, note)));
    }

    @GetMapping("/{id}/financial-eligibility")
    @Operation(summary = "Evaluate financial eligibility based on KYC and risk")
    public Map<String, String> getFinancialEligibility(@PathVariable Long id) {
        return userService.getFinancialEligibility(id);
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
