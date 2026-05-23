package com.taskflow.securetaskflow.auth;

import com.taskflow.securetaskflow.user.Role;

/**
 * Authentication response containing a bearer token and user profile.
 */
public record AuthResponse(
        String token,
        Long userId,
        String fullName,
        String email,
        Role role
) {
}
