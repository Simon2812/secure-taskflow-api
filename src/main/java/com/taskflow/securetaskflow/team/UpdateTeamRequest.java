package com.taskflow.securetaskflow.team;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request payload for changing team metadata.
 */
public record UpdateTeamRequest(
        @NotBlank @Size(max = 120) String name,
        @Size(max = 1000) String description
) {
}
