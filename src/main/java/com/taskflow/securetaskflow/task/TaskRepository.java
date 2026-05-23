package com.taskflow.securetaskflow.task;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for task persistence and project-scoped lookups.
 */
public interface TaskRepository extends JpaRepository<TaskItem, Long> {
    Page<TaskItem> findByProject_Id(Long projectId, Pageable pageable);
}
