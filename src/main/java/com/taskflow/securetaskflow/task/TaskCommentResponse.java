package com.taskflow.securetaskflow.task;

import java.time.Instant;

/**
 * API response DTO for task comments.
 */
public record TaskCommentResponse(
        Long id,
        String body,
        Long taskId,
        Long authorId,
        String authorEmail,
        Instant createdAt
) {
    public static TaskCommentResponse from(TaskComment comment) {
        return new TaskCommentResponse(
                comment.getId(),
                comment.getBody(),
                comment.getTask().getId(),
                comment.getAuthor().getId(),
                comment.getAuthor().getEmail(),
                comment.getCreatedAt()
        );
    }
}
