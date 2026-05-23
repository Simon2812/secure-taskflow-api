package com.taskflow.securetaskflow.auth;

import com.taskflow.securetaskflow.user.Role;

/**
 * Security principal projection for the authenticated API user.
 */
public record CurrentUser(Long id, String fullName, String email, Role role) {
}
