package com.taskflow.securetaskflow.task;

import java.time.Instant;
import java.time.LocalDate;

/**
 * API response DTO for task data.
 */
public record TaskResponse(
        Long id,
        String title,
        String description,
        TaskStatus status,
        TaskPriority priority,
        LocalDate dueDate,
        Long projectId,
        String projectName,
        Long assigneeId,
        String assigneeEmail,
        Long createdById,
        Instant createdAt,
        Instant updatedAt
) {
    public static TaskResponse from(TaskItem task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getPriority(),
                task.getDueDate(),
                task.getProject().getId(),
                task.getProject().getName(),
                task.getAssignee() == null ? null : task.getAssignee().getId(),
                task.getAssignee() == null ? null : task.getAssignee().getEmail(),
                task.getCreatedBy().getId(),
                task.getCreatedAt(),
                task.getUpdatedAt()
        );
    }
}
