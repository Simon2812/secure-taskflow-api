package com.taskflow.securetaskflow.team;

import jakarta.validation.constraints.NotNull;

/**
 * Request payload for adding a user to a team.
 */
public record AddTeamMemberRequest(@NotNull Long userId) {
}
