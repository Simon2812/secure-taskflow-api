package com.taskflow.securetaskflow.project;

import com.taskflow.securetaskflow.team.Team;
import com.taskflow.securetaskflow.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalDate;

/**
 * Persistent project aggregate owned by a team.
 */
@Entity
@Table(name = "projects")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 1200)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProjectStatus status = ProjectStatus.PLANNING;

    private LocalDate dueDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User createdBy;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    protected Project() {
    }

    public Project(String name, String description, LocalDate dueDate, Team team, User createdBy) {
        this.name = name;
        this.description = description;
        this.dueDate = dueDate;
        this.team = team;
        this.createdBy = createdBy;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public ProjectStatus getStatus() {
        return status;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public Team getTeam() {
        return team;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void update(String name, String description, ProjectStatus status, LocalDate dueDate) {
        this.name = name;
        this.description = description;
        // Status is part of project planning, while individual work progress
        // is tracked on TaskItem records.
        this.status = status;
        this.dueDate = dueDate;
    }
}
