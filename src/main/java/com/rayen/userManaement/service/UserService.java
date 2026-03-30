package com.rayen.userManaement.service;

import com.rayen.userManaement.entity.AssistantUser;
import com.rayen.userManaement.entity.AuditLog;
import com.rayen.userManaement.entity.KycStatus;
import com.rayen.userManaement.entity.ObserverUser;
import com.rayen.userManaement.entity.Permission;
import com.rayen.userManaement.entity.RiskLevel;
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
            observer.setRiskLevel(RiskLevel.MEDIUM);
            observer.setKycStatus(KycStatus.PENDING);
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
            String riskLevel = ((ObserverUserDTO) dto).getRiskLevel();
            if (riskLevel != null && !riskLevel.isBlank()) {
                o.setRiskLevel(parseRiskLevel(riskLevel));
            }
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

    @Transactional(readOnly = true)
    public String getObserverRiskLevel(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
        if (!(user instanceof ObserverUser observer)) {
            throw new IllegalArgumentException("Risk level is only available for OBSERVER users.");
        }
        return observer.getRiskLevel() != null ? observer.getRiskLevel().name() : RiskLevel.MEDIUM.name();
    }

    @Transactional
    public String updateObserverRiskLevel(Long id, String riskLevelValue, String actorEmail) {
        if (actorEmail == null || actorEmail.isBlank()) {
            throw new IllegalArgumentException("Non authentifie. Token requis.");
        }
        User actor = userRepository.findByEmail(actorEmail.trim())
                .orElseThrow(() -> new IllegalArgumentException("Compte acteur introuvable."));
        if (!(actor instanceof AssistantUser)) {
            throw new IllegalArgumentException("Acces refuse: seul un ASSISTANT peut modifier le risk level.");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
        if (!(user instanceof ObserverUser observer)) {
            throw new IllegalArgumentException("Risk level can only be updated for OBSERVER users.");
        }
        RiskLevel current = observer.getRiskLevel() != null ? observer.getRiskLevel() : RiskLevel.MEDIUM;
        RiskLevel target = parseRiskLevel(riskLevelValue);

        if (current == RiskLevel.HIGH && target == RiskLevel.LOW) {
            throw new IllegalArgumentException("Direct transition HIGH -> LOW is forbidden. Reduce to MEDIUM first after strong remediation.");
        }

        observer.setRiskLevel(target);
        userRepository.save(observer);
        return observer.getRiskLevel().name();
    }

    @Transactional(readOnly = true)
    public String getObserverKycStatus(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
        if (!(user instanceof ObserverUser observer)) {
            throw new IllegalArgumentException("KYC status is only available for OBSERVER users.");
        }
        return observer.getKycStatus() != null ? observer.getKycStatus().name() : KycStatus.PENDING.name();
    }

    @Transactional
    public String updateObserverKycStatus(Long id, String kycStatusValue, String actorEmail, String note) {
        if (actorEmail == null || actorEmail.isBlank()) {
            throw new IllegalArgumentException("Non authentifie. Token requis.");
        }
        User actor = userRepository.findByEmail(actorEmail.trim())
                .orElseThrow(() -> new IllegalArgumentException("Compte acteur introuvable."));
        if (!(actor instanceof AssistantUser)) {
            throw new IllegalArgumentException("Acces refuse: seul un ASSISTANT peut valider/rejeter le KYC.");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
        if (!(user instanceof ObserverUser observer)) {
            throw new IllegalArgumentException("KYC status can only be updated for OBSERVER users.");
        }

        KycStatus target = parseKycStatus(kycStatusValue);
        observer.setKycStatus(target);
        if (note != null && !note.isBlank()) {
            observer.setKycReviewNote(note.trim());
        }
        userRepository.save(observer);

        // Audit trail (mapped to observer log stream for traceability)
        AuditLog log = new AuditLog();
        log.setAction("KYC_" + target.name() + (note != null && !note.isBlank() ? " - " + note : ""));
        log.setTimestamp(LocalDateTime.now());
        log.setEventId(null);
        log.setPerformedByObserver(observer);
        auditLogRepository.save(log);

        return observer.getKycStatus().name();
    }

    @Transactional(readOnly = true)
    public KycProfileDTO getObserverKycProfile(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
        if (!(user instanceof ObserverUser observer)) {
            throw new IllegalArgumentException("KYC profile is only available for OBSERVER users.");
        }
        return new KycProfileDTO(
                observer.getNationalIdNumber(),
                observer.getDateOfBirth(),
                observer.getAddress(),
                observer.getPhoneNumber(),
                observer.getKycSubmittedAt(),
                observer.getKycReviewNote()
        );
    }

    @Transactional(readOnly = true)
    public KycProfileDTO getMyKycProfile(String actorEmail) {
        ObserverUser observer = requireObserverSelf(actorEmail);
        return getObserverKycProfile(observer.getId());
    }

    @Transactional
    public KycProfileDTO createMyKycProfile(KycProfileDTO request, String actorEmail) {
        ObserverUser observer = requireObserverSelf(actorEmail);
        if (observer.getKycSubmittedAt() != null) {
            throw new IllegalArgumentException("KYC profile already exists. Use PUT to update it.");
        }
        applyKycProfileUpdate(observer, request);
        return getObserverKycProfile(observer.getId());
    }

    @Transactional
    public KycProfileDTO updateMyKycProfile(KycProfileDTO request, String actorEmail) {
        ObserverUser observer = requireObserverSelf(actorEmail);
        if (observer.getKycSubmittedAt() == null) {
            throw new IllegalArgumentException("KYC profile does not exist yet. Use POST first.");
        }
        applyKycProfileUpdate(observer, request);
        return getObserverKycProfile(observer.getId());
    }

    private ObserverUser requireObserverSelf(String actorEmail) {
        if (actorEmail == null || actorEmail.isBlank()) {
            throw new IllegalArgumentException("Non authentifie. Token requis.");
        }
        User actor = userRepository.findByEmail(actorEmail.trim())
                .orElseThrow(() -> new IllegalArgumentException("Compte acteur introuvable."));
        if (!(actor instanceof ObserverUser observer)) {
            throw new IllegalArgumentException("Seul un OBSERVER peut gerer son profil KYC.");
        }
        return observer;
    }

    private void applyKycProfileUpdate(ObserverUser observer, KycProfileDTO request) {
        if (request.getNationalIdNumber() == null || request.getNationalIdNumber().isBlank()) {
            throw new IllegalArgumentException("nationalIdNumber (CIN) est obligatoire.");
        }
        if (request.getDateOfBirth() == null) {
            throw new IllegalArgumentException("dateOfBirth est obligatoire.");
        }
        if (request.getAddress() == null || request.getAddress().isBlank()) {
            throw new IllegalArgumentException("address est obligatoire.");
        }
        if (request.getPhoneNumber() == null || request.getPhoneNumber().isBlank()) {
            throw new IllegalArgumentException("phoneNumber est obligatoire.");
        }

        observer.setNationalIdNumber(request.getNationalIdNumber().trim());
        observer.setDateOfBirth(request.getDateOfBirth());
        observer.setAddress(request.getAddress().trim());
        observer.setPhoneNumber(request.getPhoneNumber().trim());
        observer.setKycSubmittedAt(LocalDateTime.now());
        observer.setKycStatus(KycStatus.PENDING);
        userRepository.save(observer);
    }

    @Transactional(readOnly = true)
    public java.util.Map<String, String> getFinancialEligibility(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
        if (!(user instanceof ObserverUser observer)) {
            throw new IllegalArgumentException("Financial eligibility is only available for OBSERVER users.");
        }

        KycStatus kycStatus = observer.getKycStatus() != null ? observer.getKycStatus() : KycStatus.PENDING;
        RiskLevel riskLevel = observer.getRiskLevel() != null ? observer.getRiskLevel() : RiskLevel.MEDIUM;

        String decision;
        String reason;
        if (kycStatus != KycStatus.VERIFIED) {
            decision = "BLOCKED";
            reason = "KYC_NOT_VERIFIED";
        } else if (riskLevel == RiskLevel.HIGH) {
            decision = "REVIEW";
            reason = "HIGH_RISK_PROFILE";
        } else {
            decision = "ALLOWED";
            reason = "KYC_VERIFIED_AND_RISK_ACCEPTABLE";
        }

        return java.util.Map.of(
                "kycStatus", kycStatus.name(),
                "riskLevel", riskLevel.name(),
                "decision", decision,
                "reason", reason
        );
    }

    private UserDTO toDTO(User user) {
        if (user instanceof ObserverUser) {
            ObserverUserDTO dto = new ObserverUserDTO();
            fillBaseDTO(user, dto);
            dto.setUserType("OBSERVER");
            dto.setPermissions(convertToPermissionStrings(((ObserverUser) user).getPermissions()));
            dto.setRiskLevel(((ObserverUser) user).getRiskLevel() != null ? ((ObserverUser) user).getRiskLevel().name() : RiskLevel.MEDIUM.name());
            dto.setKycStatus(((ObserverUser) user).getKycStatus() != null ? ((ObserverUser) user).getKycStatus().name() : KycStatus.PENDING.name());
            dto.setNationalIdNumber(((ObserverUser) user).getNationalIdNumber());
            dto.setDateOfBirth(((ObserverUser) user).getDateOfBirth());
            dto.setAddress(((ObserverUser) user).getAddress());
            dto.setPhoneNumber(((ObserverUser) user).getPhoneNumber());
            dto.setKycSubmittedAt(((ObserverUser) user).getKycSubmittedAt());
            dto.setKycReviewNote(((ObserverUser) user).getKycReviewNote());
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

    private RiskLevel parseRiskLevel(String riskLevelValue) {
        if (riskLevelValue == null || riskLevelValue.isBlank()) {
            return RiskLevel.MEDIUM;
        }
        try {
            return RiskLevel.valueOf(riskLevelValue.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid risk level: " + riskLevelValue + ". Valid values are LOW, MEDIUM, HIGH.");
        }
    }

    private KycStatus parseKycStatus(String kycStatusValue) {
        if (kycStatusValue == null || kycStatusValue.isBlank()) {
            throw new IllegalArgumentException("kycStatus is required. Valid values are PENDING, VERIFIED, REJECTED.");
        }
        try {
            return KycStatus.valueOf(kycStatusValue.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid kycStatus: " + kycStatusValue + ". Valid values are PENDING, VERIFIED, REJECTED.");
        }
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
