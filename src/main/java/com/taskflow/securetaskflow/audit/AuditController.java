package com.taskflow.securetaskflow.audit;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller that exposes audit history to administrators.
 */
@RestController
@RequestMapping("/api/audit-logs")
public class AuditController {

    private final AuditService auditService;

    public AuditController(AuditService auditService) {
        this.auditService = auditService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Page<AuditLogResponse> list(
            @RequestParam(required = false) String resourceType,
            @RequestParam(required = false) Long resourceId,
            Pageable pageable
    ) {
        if (resourceType != null && resourceId != null) {
            return auditService.listForResource(resourceType, resourceId, pageable);
        }
        return auditService.list(pageable);
    }
}
