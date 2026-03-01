package com.rayen.forumManagement.repository;

import com.rayen.forumManagement.entity.ForumTopic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ForumTopicRepository extends JpaRepository<ForumTopic, Long> {

    List<ForumTopic> findByCreatedBy_IdOrderByCreatedAtDesc(Long createdById);
}
