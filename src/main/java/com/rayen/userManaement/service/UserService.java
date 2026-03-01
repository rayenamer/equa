package com.rayen.userManaement.service;

import com.rayen.userManaement.entity.AssistantUser;
import com.rayen.userManaement.entity.AuditLog;
import com.rayen.userManaement.entity.ObserverUser;
import com.rayen.userManaement.entity.Permission;
import com.rayen.userManaement.entity.User;
import com.rayen.userManaement.model.*;
import com.rayen.userManaement.repository.AuditLogRepository;
import com.rayen.userManaement.repository.ObserverUserRepository;
import com.rayen.userManaement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ObserverUserRepository observerUserRepository;
    private final AuditLogRepository auditLogRepository;

    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserDTO getById(Long id) {
        return userRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
    }

    @Transactional
    public UserDTO createUser(CreateUserRequest request) {
        if (request.getEmail() != null && !request.getEmail().isBlank() && userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already in use: " + request.getEmail());
        }
        User user;
        if ("OBSERVER".equalsIgnoreCase(request.getUserType())) {
            ObserverUser observer = new ObserverUser();
            observer.setUsername(request.getUsername());
            observer.setEmail(request.getEmail());
            if (request.getPermissions() != null && !request.getPermissions().isEmpty()) {
                observer.setPermissions(convertToPermissionEnums(request.getPermissions()));
            }
            user = observerUserRepository.save(observer);
        } else {
            AssistantUser assistant = new AssistantUser();
            assistant.setUsername(request.getUsername());
            assistant.setEmail(request.getEmail());
            user = userRepository.save(assistant);
        }
        return toDTO(user);
    }

    @Transactional
    public UserDTO updateUser(Long id, UserDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
        if (dto.getUsername() != null) user.setUsername(dto.getUsername());
        if (dto.getEmail() != null) user.setEmail(dto.getEmail());
        if (user instanceof ObserverUser && dto instanceof ObserverUserDTO) {
            ObserverUser o = (ObserverUser) user;
            List<String> perms = ((ObserverUserDTO) dto).getPermissions();
            if (perms != null) o.setPermissions(convertToPermissionEnums(perms));
        }
        user = userRepository.save(user);
        return toDTO(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("User not found: " + id);
        }
        userRepository.deleteById(id);
    }

    // --- Audit log ---

    @Transactional
    public AuditLogDTO logAction(Long observerUserId, String action, Integer eventId) {
        ObserverUser observer = observerUserRepository.findById(observerUserId)
                .orElseThrow(() -> new IllegalArgumentException("Observer user not found: " + observerUserId));
        AuditLog log = new AuditLog();
        log.setAction(action);
        log.setTimestamp(LocalDateTime.now());
        log.setEventId(eventId);
        log.setPerformedByObserver(observer);
        log = auditLogRepository.save(log);
        return toAuditLogDTO(log);
    }

    @Transactional(readOnly = true)
    public List<AuditLogDTO> getAuditLogsByObserver(Long observerUserId) {
        return auditLogRepository.findByPerformedByObserver_IdOrderByTimestampDesc(observerUserId).stream()
                .map(this::toAuditLogDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AuditLogDTO> getAllAuditLogs() {
        return auditLogRepository.findAll().stream()
                .map(this::toAuditLogDTO)
                .collect(Collectors.toList());
    }

    // --- Roles management ---

    @Transactional(readOnly = true)
    public UserRolesDTO getUserRoles(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
        UserRolesDTO rolesDTO = new UserRolesDTO();
        if (user instanceof ObserverUser) {
            rolesDTO.setUserType("OBSERVER");
            rolesDTO.setPermissions(convertToPermissionStrings(((ObserverUser) user).getPermissions()));
        } else if (user instanceof AssistantUser) {
            rolesDTO.setUserType("ASSISTANT");
            rolesDTO.setPermissions(null);
        } else {
            rolesDTO.setUserType("USER");
            rolesDTO.setPermissions(null);
        }
        return rolesDTO;
    }

    @Transactional
    public UserRolesDTO updateUserRoles(Long id, UserRolesDTO rolesDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
        
        // Update permissions if user is ObserverUser
        if (user instanceof ObserverUser && rolesDTO.getPermissions() != null) {
            ((ObserverUser) user).setPermissions(convertToPermissionEnums(rolesDTO.getPermissions()));
            userRepository.save(user);
        }
        
        return getUserRoles(id);
    }

    private UserDTO toDTO(User user) {
        if (user instanceof ObserverUser) {
            ObserverUserDTO dto = new ObserverUserDTO();
            fillBaseDTO(user, dto);
            dto.setUserType("OBSERVER");
            dto.setPermissions(convertToPermissionStrings(((ObserverUser) user).getPermissions()));
            return dto;
        }
        if (user instanceof AssistantUser) {
            AssistantUserDTO dto = new AssistantUserDTO();
            fillBaseDTO(user, dto);
            dto.setUserType("ASSISTANT");
            return dto;
        }
        UserDTO dto = new UserDTO();
        fillBaseDTO(user, dto);
        dto.setUserType("USER");
        return dto;
    }

    // Conversion helpers
    private List<Permission> convertToPermissionEnums(List<String> permissionStrings) {
        if (permissionStrings == null) return new ArrayList<>();
        return permissionStrings.stream()
                .map(str -> {
                    try {
                        return Permission.valueOf(str.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        throw new IllegalArgumentException("Invalid permission: " + str + ". Valid permissions are: " + 
                            java.util.Arrays.toString(Permission.values()));
                    }
                })
                .collect(Collectors.toList());
    }

    private List<String> convertToPermissionStrings(List<Permission> permissions) {
        if (permissions == null) return null;
        return permissions.stream()
                .map(Permission::name)
                .collect(Collectors.toList());
    }

    private void fillBaseDTO(User user, UserDTO dto) {
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
    }

    private AuditLogDTO toAuditLogDTO(AuditLog log) {
        AuditLogDTO dto = new AuditLogDTO();
        dto.setLogId(log.getLogId());
        dto.setAction(log.getAction());
        dto.setPerformedBy(log.getPerformedBy());
        dto.setTimestamp(log.getTimestamp());
        dto.setEventId(log.getEventId());
        dto.setObserverUserId(log.getPerformedByObserver() != null ? log.getPerformedByObserver().getId() : null);
        return dto;
    }
}
