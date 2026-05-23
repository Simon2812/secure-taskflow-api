package com.taskflow.securetaskflow.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

/**
 * Request payload for creating a task inside a project.
 */
public record CreateTaskRequest(
        @NotNull Long projectId,
        @NotBlank @Size(max = 180) String title,
        @Size(max = 2000) String description,
        TaskPriority priority,
        LocalDate dueDate,
        Long assigneeId
) {
}
