package com.taskflow.securetaskflow.team;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request payload for creating a team.
 */
public record CreateTeamRequest(
        @NotBlank @Size(max = 120) String name,
        @Size(max = 1000) String description
) {
}
