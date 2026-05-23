package com.taskflow.securetaskflow.task;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for loading comments by task.
 */
public interface TaskCommentRepository extends JpaRepository<TaskComment, Long> {
    List<TaskComment> findByTask_IdOrderByCreatedAtAsc(Long taskId);
}
