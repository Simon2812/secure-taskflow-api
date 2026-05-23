package com.taskflow.securetaskflow.task;

import jakarta.validation.constraints.NotNull;

/**
 * Request payload for changing a task status.
 */
public record ChangeTaskStatusRequest(@NotNull TaskStatus status) {
}
