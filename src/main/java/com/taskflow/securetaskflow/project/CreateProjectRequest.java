package com.taskflow.securetaskflow.project;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

/**
 * Request payload for creating a project within a team.
 */
public record CreateProjectRequest(
        @NotNull Long teamId,
        @NotBlank @Size(max = 160) String name,
        @Size(max = 1200) String description,
        LocalDate dueDate
) {
}
