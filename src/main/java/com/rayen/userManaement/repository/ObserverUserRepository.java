package com.rayen.userManaement.repository;

import com.rayen.userManaement.entity.ObserverUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ObserverUserRepository extends JpaRepository<ObserverUser, Long> {

    List<ObserverUser> findByPermissionsContaining(String permission);
}
