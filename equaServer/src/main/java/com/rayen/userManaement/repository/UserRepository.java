package com.rayen.userManaement.repository;

import com.rayen.userManaement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<User> findByEmailAndAuthProvider(String email, String authProvider);

    Optional<User> findByProviderIdAndAuthProvider(String providerId, String authProvider);

    Optional<User> findByPasswordResetTokenAndPasswordResetUsedAtIsNull(String token);
}
