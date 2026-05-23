package com.taskflow.securetaskflow.project;

import java.time.Instant;
import java.time.LocalDate;

/**
 * API response DTO for project data.
 */
public record ProjectResponse(
        Long id,
        String name,
        String description,
        ProjectStatus status,
        LocalDate dueDate,
        Long teamId,
        String teamName,
        Long createdById,
        Instant createdAt
) {
    public static ProjectResponse from(Project project) {
        return new ProjectResponse(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getStatus(),
                project.getDueDate(),
                project.getTeam().getId(),
                project.getTeam().getName(),
                project.getCreatedBy().getId(),
                project.getCreatedAt()
        );
    }
}
