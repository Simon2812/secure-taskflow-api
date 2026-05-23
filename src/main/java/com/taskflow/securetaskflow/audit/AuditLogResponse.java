package com.taskflow.securetaskflow.audit;

import java.time.Instant;

/**
 * API response DTO for audit log entries.
 */
public record AuditLogResponse(
        Long id,
        String action,
        String resourceType,
        Long resourceId,
        String details,
        Long actorId,
        String actorEmail,
        Instant createdAt
) {
    static AuditLogResponse from(AuditLog auditLog) {
        return new AuditLogResponse(
                auditLog.getId(),
                auditLog.getAction(),
                auditLog.getResourceType(),
                auditLog.getResourceId(),
                auditLog.getDetails(),
                auditLog.getActor().getId(),
                auditLog.getActor().getEmail(),
                auditLog.getCreatedAt()
        );
    }
}
