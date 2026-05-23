package com.taskflow.securetaskflow.user;

import java.time.Instant;

/**
 * API response DTO for user profile data.
 */
public record UserResponse(
        Long id,
        String fullName,
        String email,
        Role role,
        Instant createdAt
) {
    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getFullName(), user.getEmail(), user.getRole(), user.getCreatedAt());
    }
}
