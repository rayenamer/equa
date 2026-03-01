package com.rayen.forumManagement.repository;

import com.rayen.forumManagement.entity.ForumMessage;
import com.rayen.forumManagement.entity.ForumReport;
import com.rayen.forumManagement.entity.ForumReportStatus;
import com.rayen.forumManagement.entity.ForumTopic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ForumReportRepository extends JpaRepository<ForumReport, Long> {

    long countByTopicAndStatus(ForumTopic topic, ForumReportStatus status);

    long countByMessageAndStatus(ForumMessage message, ForumReportStatus status);

    List<ForumReport> findByStatusOrderByCreatedAtAsc(ForumReportStatus status);

    List<ForumReport> findByTopic_TopicIdOrderByCreatedAtAsc(Long topicId);

    List<ForumReport> findByMessage_MessageIdOrderByCreatedAtAsc(Long messageId);
}

