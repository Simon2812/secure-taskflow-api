package com.taskflow.securetaskflow.user;

import com.taskflow.securetaskflow.audit.AuditService;
import com.taskflow.securetaskflow.common.ResourceNotFoundException;
import com.taskflow.securetaskflow.common.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service for user profile lookup and administrative role updates.
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final SecurityUtils securityUtils;
    private final AuditService auditService;

    public UserService(UserRepository userRepository, SecurityUtils securityUtils, AuditService auditService) {
        this.userRepository = userRepository;
        this.securityUtils = securityUtils;
        this.auditService = auditService;
    }

    public Page<UserResponse> list(Pageable pageable) {
        return userRepository.findAll(pageable).map(UserResponse::from);
    }

    public UserResponse me() {
        // Build the profile response from the authenticated principal instead
        // of accepting a user id from the request.
        return UserResponse.from(securityUtils.currentUser());
    }

    public UserResponse updateRole(Long userId, UpdateRoleRequest request) {
        User actor = securityUtils.currentUser();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        // Role changes are persisted before the audit event so the audit log
        // points at the final role state.
        user.updateRole(request.role());
        User saved = userRepository.save(user);
        auditService.record("USER_ROLE_UPDATED", "USER", saved.getId(), "Role changed to " + request.role(), actor);
        return UserResponse.from(saved);
    }
}
