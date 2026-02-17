package com.rayen.userManaement.repository;

import com.rayen.userManaement.entity.AssistantUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssistantUserRepository extends JpaRepository<AssistantUser, Long> {
}
