package com.taskflow.securetaskflow.project;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

/**
 * Request payload for updating project details and status.
 */
public record UpdateProjectRequest(
        @NotBlank @Size(max = 160) String name,
        @Size(max = 1200) String description,
        @NotNull ProjectStatus status,
        LocalDate dueDate
) {
}
