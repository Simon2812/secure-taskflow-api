package com.taskflow.securetaskflow.audit;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for querying persisted audit log records.
 */
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    Page<AuditLog> findByResourceTypeAndResourceId(String resourceType, Long resourceId, Pageable pageable);
}
