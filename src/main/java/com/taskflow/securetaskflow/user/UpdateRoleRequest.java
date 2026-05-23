package com.taskflow.securetaskflow.user;

import jakarta.validation.constraints.NotNull;

/**
 * Request payload for administrator role changes.
 */
public record UpdateRoleRequest(@NotNull Role role) {
}
