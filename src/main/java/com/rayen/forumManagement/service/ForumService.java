package com.rayen.forumManagement.service;

import com.rayen.forumManagement.entity.ForumMessage;
import com.rayen.forumManagement.entity.ForumReport;
import com.rayen.forumManagement.entity.ForumReportStatus;
import com.rayen.forumManagement.entity.ForumReportTargetType;
import com.rayen.forumManagement.entity.ForumTopic;
import com.rayen.forumManagement.model.*;
import com.rayen.forumManagement.repository.ForumMessageRepository;
import com.rayen.forumManagement.repository.ForumReportRepository;
import com.rayen.forumManagement.repository.ForumTopicRepository;
import com.rayen.userManaement.entity.AssistantUser;
import com.rayen.userManaement.entity.User;
import com.rayen.userManaement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ForumService {

    private final ForumTopicRepository forumTopicRepository;
    private final ForumMessageRepository forumMessageRepository;
    private final ForumReportRepository forumReportRepository;
    private final UserRepository userRepository;
    private final InappropriateLanguageFilterService languageFilterService;

    @Value("${app.forum.report.threshold:3}")
    private int reportThreshold;

    @Transactional(readOnly = true)
    public List<ForumTopicDTO> getAllTopics() {
        return forumTopicRepository.findAll().stream()
                .filter(t -> !t.isHidden())
                .map(this::toTopicDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ForumTopicDTO getTopicById(Long id) {
        return forumTopicRepository.findById(id)
                .map(this::toTopicDTOWithMessages)
                .orElseThrow(() -> new IllegalArgumentException("Forum topic not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<ForumTopicDTO> getTopicsByUser(Long userId) {
        return forumTopicRepository.findByCreatedBy_IdOrderByCreatedAtDesc(userId).stream()
                .map(this::toTopicDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ForumTopicDTO createTopic(CreateForumTopicRequest request, String creatorEmail) {
        if (creatorEmail == null || creatorEmail.isBlank()) {
            throw new IllegalArgumentException("Authenticated user email is required to create a topic");
        }
        User createdBy = userRepository.findByEmail(creatorEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found for email: " + creatorEmail));

        ForumTopic topic = new ForumTopic();
        topic.setTitle(request.getTitle());
        topic.setDescription(request.getDescription());
        topic.setCreatedBy(createdBy);

        // Filtrage de langage inapproprié sur le titre + description
        String textToCheck = (request.getTitle() == null ? "" : request.getTitle()) + " " +
                (request.getDescription() == null ? "" : request.getDescription());
        boolean suspicious = languageFilterService.isSuspicious(textToCheck);
        if (suspicious) {
            topic.setHidden(true);
        }

        topic = forumTopicRepository.save(topic);

        // Si le contenu est suspect, créer aussi un report automatique pour l'historique/modération
        if (suspicious) {
            ForumReport autoReport = new ForumReport();
            autoReport.setTargetType(ForumReportTargetType.TOPIC);
            autoReport.setTopic(topic);
            autoReport.setReporter(createdBy);
            autoReport.setReason("Signalement automatique: langage potentiellement inapproprié dans le titre ou la description.");
            autoReport.setStatus(ForumReportStatus.PENDING);
            autoReport.setAutoHidden(true);
            forumReportRepository.save(autoReport);
        }

        return toTopicDTO(topic);
    }

    @Transactional
    public ForumTopicDTO updateTopic(Long id, UpdateForumTopicRequest request) {
        ForumTopic topic = forumTopicRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Forum topic not found: " + id));

        if (request.getTitle() != null && !request.getTitle().isBlank()) {
            topic.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            topic.setDescription(request.getDescription());
        }

        topic = forumTopicRepository.save(topic);
        return toTopicDTO(topic);
    }

    @Transactional
    public void deleteTopic(Long id) {
        ForumTopic topic = forumTopicRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Forum topic not found: " + id));

        // Supprimer d'abord les reports liés à ce topic pour respecter les contraintes FK
        List<ForumReport> reportsForTopic = forumReportRepository.findByTopic_TopicIdOrderByCreatedAtAsc(id);
        if (!reportsForTopic.isEmpty()) {
            forumReportRepository.deleteAll(reportsForTopic);
        }

        forumTopicRepository.delete(topic);
    }

    @Transactional
    public ForumMessageDTO addMessage(Long topicId, CreateForumMessageRequest request, String authorEmail) {
        ForumTopic topic = forumTopicRepository.findById(topicId)
                .orElseThrow(() -> new IllegalArgumentException("Forum topic not found: " + topicId));
        if (authorEmail == null || authorEmail.isBlank()) {
            throw new IllegalArgumentException("Authenticated user email is required to add a message");
        }
        User author = userRepository.findByEmail(authorEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found for email: " + authorEmail));

        ForumMessage message = new ForumMessage();
        message.setTopic(topic);
        message.setAuthor(author);
        message.setMessageText(request.getMessageText());
        message.setGifUrl(request.getGifUrl());

        // Filtrage de langage inapproprié sur le texte du message (les emojis sont autorisés)
        boolean suspicious = languageFilterService.isSuspicious(request.getMessageText());
        if (suspicious) {
            message.setHidden(true);
        }

        message = forumMessageRepository.save(message);

        // Si le message est suspect, créer un report automatique pour l'historique/modération
        if (suspicious) {
            ForumReport autoReport = new ForumReport();
            autoReport.setTargetType(ForumReportTargetType.MESSAGE);
            autoReport.setMessage(message);
            autoReport.setReporter(author);
            autoReport.setReason("Signalement automatique: langage potentiellement inapproprié dans le message.");
            autoReport.setStatus(ForumReportStatus.PENDING);
            autoReport.setAutoHidden(true);
            forumReportRepository.save(autoReport);
        }

        return toMessageDTO(message);
    }

    @Transactional(readOnly = true)
    public List<ForumMessageDTO> getMessages(Long topicId) {
        return forumMessageRepository.findByTopic_TopicIdOrderByCreatedAtAsc(topicId).stream()
                .filter(m -> !m.isHidden())
                .map(this::toMessageDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ForumMessageDTO updateMessage(Long messageId, UpdateForumMessageRequest request) {
        ForumMessage message = forumMessageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("Forum message not found: " + messageId));
        if (request.getMessageText() != null && !request.getMessageText().isBlank()) {
            message.setMessageText(request.getMessageText());

            // Réappliquer le filtrage sur le nouveau contenu (les emojis restent autorisés)
            boolean suspicious = languageFilterService.isSuspicious(request.getMessageText());
            message.setHidden(suspicious);
        }
        if (request.getGifUrl() != null) {
            message.setGifUrl(request.getGifUrl());
        }
        message = forumMessageRepository.save(message);
        return toMessageDTO(message);
    }

    @Transactional
    public void deleteMessage(Long messageId) {
        if (!forumMessageRepository.existsById(messageId)) {
            throw new IllegalArgumentException("Forum message not found: " + messageId);
        }
        forumMessageRepository.deleteById(messageId);
    }

    @Transactional
    public ForumReportDTO reportTopic(Long topicId, CreateForumReportRequest request, String reporterEmail) {
        ForumTopic topic = forumTopicRepository.findById(topicId)
                .orElseThrow(() -> new IllegalArgumentException("Forum topic not found: " + topicId));
        if (reporterEmail == null || reporterEmail.isBlank()) {
            throw new IllegalArgumentException("Authenticated user email is required to report a topic");
        }
        User reporter = userRepository.findByEmail(reporterEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found for email: " + reporterEmail));

        ForumReport report = new ForumReport();
        report.setTargetType(ForumReportTargetType.TOPIC);
        report.setTopic(topic);
        report.setReporter(reporter);
        report.setReason(request.getReason());
        report.setStatus(ForumReportStatus.PENDING);
        report = forumReportRepository.save(report);

        autoHideTopicIfThresholdReached(topic, report);
        return toReportDTO(report);
    }

    @Transactional
    public ForumReportDTO reportMessage(Long messageId, CreateForumReportRequest request, String reporterEmail) {
        ForumMessage message = forumMessageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("Forum message not found: " + messageId));
        if (reporterEmail == null || reporterEmail.isBlank()) {
            throw new IllegalArgumentException("Authenticated user email is required to report a message");
        }
        User reporter = userRepository.findByEmail(reporterEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found for email: " + reporterEmail));

        ForumReport report = new ForumReport();
        report.setTargetType(ForumReportTargetType.MESSAGE);
        report.setMessage(message);
        report.setReporter(reporter);
        report.setReason(request.getReason());
        report.setStatus(ForumReportStatus.PENDING);
        report = forumReportRepository.save(report);

        autoHideMessageIfThresholdReached(message, report);
        return toReportDTO(report);
    }

    @Transactional(readOnly = true)
    public List<ForumReportDTO> getPendingReports() {
        return forumReportRepository.findByStatusOrderByCreatedAtAsc(ForumReportStatus.PENDING)
                .stream()
                .map(this::toReportDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ForumReportDTO> getReportsForTopic(Long topicId) {
        return forumReportRepository.findByTopic_TopicIdOrderByCreatedAtAsc(topicId).stream()
                .map(this::toReportDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ForumReportDTO> getReportsForMessage(Long messageId) {
        return forumReportRepository.findByMessage_MessageIdOrderByCreatedAtAsc(messageId).stream()
                .map(this::toReportDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ForumReportDTO moderateReport(Long reportId, ModerateForumReportRequest request, String moderatorEmail) {
        ForumReport report = forumReportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("Forum report not found: " + reportId));
        if (moderatorEmail == null || moderatorEmail.isBlank()) {
            throw new IllegalArgumentException("Authenticated user email is required to moderate a report");
        }
        User moderator = userRepository.findByEmail(moderatorEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found for email: " + moderatorEmail));

        if (!(moderator instanceof AssistantUser)) {
            throw new IllegalArgumentException("Only assistants can moderate reports");
        }

        String action = request.getAction() != null ? request.getAction().toUpperCase() : "IGNORE";
        if ("HIDE".equals(action)) {
            // Masquage manuel
            if (report.getTargetType() == ForumReportTargetType.TOPIC && report.getTopic() != null) {
                ForumTopic topic = report.getTopic();
                if (!topic.isHidden()) {
                    topic.setHidden(true);
                    forumTopicRepository.save(topic);
                }
            } else if (report.getTargetType() == ForumReportTargetType.MESSAGE && report.getMessage() != null) {
                ForumMessage message = report.getMessage();
                if (!message.isHidden()) {
                    message.setHidden(true);
                    forumMessageRepository.save(message);
                }
            }
            report.setStatus(ForumReportStatus.RESOLVED);
        } else if ("IGNORE".equals(action)) {
            report.setStatus(ForumReportStatus.REJECTED);
        } else {
            throw new IllegalArgumentException("Unknown moderation action: " + request.getAction());
        }

        report.setModerator(moderator);
        report.setModeratedAt(LocalDateTime.now());
        report.setModerationNote(request.getNote());
        report = forumReportRepository.save(report);
        return toReportDTO(report);
    }

    private void autoHideTopicIfThresholdReached(ForumTopic topic, ForumReport triggeringReport) {
        long pendingCount = forumReportRepository.countByTopicAndStatus(topic, ForumReportStatus.PENDING);
        if (!topic.isHidden() && pendingCount >= reportThreshold) {
            topic.setHidden(true);
            forumTopicRepository.save(topic);
            triggeringReport.setAutoHidden(true);
            forumReportRepository.save(triggeringReport);
        }
    }

    private void autoHideMessageIfThresholdReached(ForumMessage message, ForumReport triggeringReport) {
        long pendingCount = forumReportRepository.countByMessageAndStatus(message, ForumReportStatus.PENDING);
        if (!message.isHidden() && pendingCount >= reportThreshold) {
            message.setHidden(true);
            forumMessageRepository.save(message);
            triggeringReport.setAutoHidden(true);
            forumReportRepository.save(triggeringReport);
        }
    }

    private ForumTopicDTO toTopicDTO(ForumTopic t) {
        ForumTopicDTO dto = new ForumTopicDTO();
        dto.setTopicId(t.getTopicId());
        dto.setTitle(t.getTitle());
        dto.setDescription(t.getDescription());
        dto.setCreatedById(t.getCreatedBy() != null ? t.getCreatedBy().getId() : null);
        dto.setCreatedByUsername(t.getCreatedBy() != null ? t.getCreatedBy().getUsername() : null);
        dto.setCreatedAt(t.getCreatedAt());
        dto.setUpdatedAt(t.getUpdatedAt());
        dto.setHidden(t.isHidden());
        dto.setMessages(new ArrayList<>());
        return dto;
    }

    private ForumTopicDTO toTopicDTOWithMessages(ForumTopic t) {
        ForumTopicDTO dto = toTopicDTO(t);
        List<ForumMessageDTO> messageDTOs = forumMessageRepository
                .findByTopic_TopicIdOrderByCreatedAtAsc(t.getTopicId()).stream()
                .map(this::toMessageDTO)
                .collect(Collectors.toList());
        dto.setMessages(messageDTOs);
        return dto;
    }

    private ForumMessageDTO toMessageDTO(ForumMessage m) {
        ForumMessageDTO dto = new ForumMessageDTO();
        dto.setMessageId(m.getMessageId());
        dto.setTopicId(m.getTopic() != null ? m.getTopic().getTopicId() : null);
        dto.setAuthorId(m.getAuthor() != null ? m.getAuthor().getId() : null);
        dto.setAuthorUsername(m.getAuthor() != null ? m.getAuthor().getUsername() : null);
        dto.setMessageText(m.getMessageText());
        dto.setCreatedAt(m.getCreatedAt());
        dto.setGifUrl(m.getGifUrl());
        dto.setHidden(m.isHidden());
        return dto;
    }

    private ForumReportDTO toReportDTO(ForumReport r) {
        ForumReportDTO dto = new ForumReportDTO();
        dto.setReportId(r.getReportId());
        dto.setTargetType(r.getTargetType());
        dto.setTopicId(r.getTopic() != null ? r.getTopic().getTopicId() : null);
        dto.setMessageId(r.getMessage() != null ? r.getMessage().getMessageId() : null);
        dto.setReporterId(r.getReporter() != null ? r.getReporter().getId() : null);
        dto.setReporterUsername(r.getReporter() != null ? r.getReporter().getUsername() : null);
        dto.setReason(r.getReason());
        dto.setStatus(r.getStatus());
        dto.setAutoHidden(r.isAutoHidden());
        dto.setCreatedAt(r.getCreatedAt());
        dto.setModeratorId(r.getModerator() != null ? r.getModerator().getId() : null);
        dto.setModeratorUsername(r.getModerator() != null ? r.getModerator().getUsername() : null);
        dto.setModeratedAt(r.getModeratedAt());
        dto.setModerationNote(r.getModerationNote());
        return dto;
    }
}
