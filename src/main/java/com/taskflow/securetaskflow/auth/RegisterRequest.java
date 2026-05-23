package com.taskflow.securetaskflow.auth;

import com.taskflow.securetaskflow.user.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Registration request payload for creating a new user account.
 */
public record RegisterRequest(
        @NotBlank @Size(max = 120) String fullName,
        @Email @NotBlank String email,
        @NotBlank @Size(min = 8, max = 100) String password,
        Role role
) {
}
