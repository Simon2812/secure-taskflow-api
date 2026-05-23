package com.taskflow.securetaskflow.user;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for user persistence and email lookups.
 */
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
