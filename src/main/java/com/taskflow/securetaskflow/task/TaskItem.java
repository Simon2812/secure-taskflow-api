package com.taskflow.securetaskflow.task;

import com.taskflow.securetaskflow.project.Project;
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
 * Persistent task aggregate with assignment, priority and workflow state.
 */
@Entity
@Table(name = "tasks")
public class TaskItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status = TaskStatus.TODO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskPriority priority = TaskPriority.MEDIUM;

    private LocalDate dueDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    private User assignee;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User createdBy;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    private Instant updatedAt = Instant.now();

    protected TaskItem() {
    }

    public TaskItem(String title, String description, TaskPriority priority, LocalDate dueDate,
                    Project project, User assignee, User createdBy) {
        this.title = title;
        this.description = description;
        // Default priority keeps task creation simple while preserving an
        // explicit value in persisted records.
        this.priority = priority == null ? TaskPriority.MEDIUM : priority;
        this.dueDate = dueDate;
        this.project = project;
        this.assignee = assignee;
        this.createdBy = createdBy;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public TaskPriority getPriority() {
        return priority;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public Project getProject() {
        return project;
    }

    public User getAssignee() {
        return assignee;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void update(String title, String description, TaskPriority priority, LocalDate dueDate, User assignee) {
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.dueDate = dueDate;
        this.assignee = assignee;
        // updatedAt tracks user-visible changes without requiring an audit
        // lookup for every task listing.
        this.updatedAt = Instant.now();
    }

    public void changeStatus(TaskStatus status) {
        this.status = status;
        this.updatedAt = Instant.now();
    }
}
