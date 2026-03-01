package com.rayen.forumManagement.repository;

import com.rayen.forumManagement.entity.ForumMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ForumMessageRepository extends JpaRepository<ForumMessage, Long> {

    List<ForumMessage> findByTopic_TopicIdOrderByCreatedAtAsc(Long topicId);
}
