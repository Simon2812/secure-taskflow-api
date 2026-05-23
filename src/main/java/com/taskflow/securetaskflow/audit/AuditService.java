package com.taskflow.securetaskflow.audit;

import com.taskflow.securetaskflow.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service responsible for recording auditable domain events.
 */
@Service
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    public AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void record(String action, String resourceType, Long resourceId, String details, User actor) {
        // Audit writes are intentionally append-only; callers describe the
        // domain event and the service handles persistence.
        auditLogRepository.save(new AuditLog(action, resourceType, resourceId, details, actor));
    }

    public Page<AuditLogResponse> list(Pageable pageable) {
        // Pagination avoids returning the entire audit trail as the service
        // accumulates workflow history.
        return auditLogRepository.findAll(pageable).map(AuditLogResponse::from);
    }

    public Page<AuditLogResponse> listForResource(String resourceType, Long resourceId, Pageable pageable) {
        return auditLogRepository.findByResourceTypeAndResourceId(resourceType, resourceId, pageable)
                .map(AuditLogResponse::from);
    }
}
