package com.taskflow.securetaskflow.common;

import com.taskflow.securetaskflow.user.User;
import com.taskflow.securetaskflow.user.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Authorization helper methods shared by service-layer workflows.
 */
@Component
public class SecurityUtils {

    private final UserRepository userRepository;

    public SecurityUtils(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof User user)) {
            throw new ForbiddenActionException("Authenticated user is required");
        }
        return userRepository.findById(user.getId())
                .orElseThrow(() -> new ForbiddenActionException("Authenticated user no longer exists"));
    }
}
