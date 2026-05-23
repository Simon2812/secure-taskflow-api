package com.taskflow.securetaskflow.task;

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
 * Persistent comment attached to a task by a team member.
 */
@Entity
@Table(name = "task_comments")
public class TaskComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 2000)
    private String body;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private TaskItem task;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User author;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    protected TaskComment() {
    }

    public TaskComment(String body, TaskItem task, User author) {
        this.body = body;
        this.task = task;
        this.author = author;
    }

    public Long getId() {
        return id;
    }

    public String getBody() {
        return body;
    }

    public TaskItem getTask() {
        return task;
    }

    public User getAuthor() {
        return author;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
