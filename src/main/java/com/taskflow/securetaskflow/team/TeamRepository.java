package com.taskflow.securetaskflow.team;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for team persistence and membership queries.
 */
public interface TeamRepository extends JpaRepository<Team, Long> {
    boolean existsByName(String name);

    List<Team> findByMembers_Id(Long memberId);
}
