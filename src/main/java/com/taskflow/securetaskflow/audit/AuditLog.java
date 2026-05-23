package com.taskflow.securetaskflow.audit;

import com.taskflow.securetaskflow.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;

/**
 * Persistent audit entry describing a user action on a domain resource.
 */
@Entity
@Table(name = "audit_logs")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String action;

    @Column(nullable = false)
    private String resourceType;

    @Column(nullable = false)
    private Long resourceId;

    @Column(length = 1500)
    private String details;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User actor;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    protected AuditLog() {
    }

    public AuditLog(String action, String resourceType, Long resourceId, String details, User actor) {
        this.action = action;
        this.resourceType = resourceType;
        this.resourceId = resourceId;
        this.details = details;
        // Store the actor relation instead of only an email so reports can
        // still show user metadata from the current database state.
        this.actor = actor;
    }

    public Long getId() {
        return id;
    }

    public String getAction() {
        return action;
    }

    public String getResourceType() {
        return resourceType;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public String getDetails() {
        return details;
    }

    public User getActor() {
        return actor;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
