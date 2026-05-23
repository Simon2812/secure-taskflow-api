package com.taskflow.securetaskflow.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Login request payload containing user credentials.
 */
public record AuthRequest(
        @Email @NotBlank String email,
        @NotBlank String password
) {
}
