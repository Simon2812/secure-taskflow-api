package com.taskflow.securetaskflow.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request payload for adding a task comment.
 */
public record CreateCommentRequest(@NotBlank @Size(max = 2000) String body) {
}
