package com.taskflow.securetaskflow.project;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for project persistence and team-scoped lookups.
 */
public interface ProjectRepository extends JpaRepository<Project, Long> {
    Page<Project> findByTeam_Members_Id(Long memberId, Pageable pageable);
}
