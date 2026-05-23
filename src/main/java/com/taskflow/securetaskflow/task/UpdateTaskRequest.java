package com.taskflow.securetaskflow.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

/**
 * Request payload for updating task details.
 */
public record UpdateTaskRequest(
        @NotBlank @Size(max = 180) String title,
        @Size(max = 2000) String description,
        @NotNull TaskPriority priority,
        LocalDate dueDate,
        Long assigneeId
) {
}
