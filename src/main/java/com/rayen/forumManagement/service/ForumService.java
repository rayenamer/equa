package com.rayen.forumManagement.service;

import com.rayen.forumManagement.entity.ForumMessage;
import com.rayen.forumManagement.entity.ForumTopic;
import com.rayen.forumManagement.model.*;
import com.rayen.forumManagement.repository.ForumMessageRepository;
import com.rayen.forumManagement.repository.ForumTopicRepository;
import com.rayen.userManaement.entity.User;
import com.rayen.userManaement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ForumService {

    private final ForumTopicRepository forumTopicRepository;
    private final ForumMessageRepository forumMessageRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<ForumTopicDTO> getAllTopics() {
        return forumTopicRepository.findAll().stream()
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
    public ForumTopicDTO createTopic(CreateForumTopicRequest request) {
        User createdBy = userRepository.findById(request.getCreatedById())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + request.getCreatedById()));

        ForumTopic topic = new ForumTopic();
        topic.setTitle(request.getTitle());
        topic.setDescription(request.getDescription());
        topic.setCreatedBy(createdBy);

        topic = forumTopicRepository.save(topic);
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
        if (!forumTopicRepository.existsById(id)) {
            throw new IllegalArgumentException("Forum topic not found: " + id);
        }
        forumTopicRepository.deleteById(id);
    }

    @Transactional
    public ForumMessageDTO addMessage(Long topicId, CreateForumMessageRequest request) {
        ForumTopic topic = forumTopicRepository.findById(topicId)
                .orElseThrow(() -> new IllegalArgumentException("Forum topic not found: " + topicId));
        User author = userRepository.findById(request.getAuthorId())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + request.getAuthorId()));

        ForumMessage message = new ForumMessage();
        message.setTopic(topic);
        message.setAuthor(author);
        message.setMessageText(request.getMessageText());

        message = forumMessageRepository.save(message);
        return toMessageDTO(message);
    }

    @Transactional(readOnly = true)
    public List<ForumMessageDTO> getMessages(Long topicId) {
        return forumMessageRepository.findByTopic_TopicIdOrderByCreatedAtAsc(topicId).stream()
                .map(this::toMessageDTO)
                .collect(Collectors.toList());
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
        return dto;
    }
}
