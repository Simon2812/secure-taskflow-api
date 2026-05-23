package com.taskflow.securetaskflow.task;

import com.taskflow.securetaskflow.audit.AuditService;
import com.taskflow.securetaskflow.common.BadRequestException;
import com.taskflow.securetaskflow.common.SecurityUtils;
import com.taskflow.securetaskflow.common.ResourceNotFoundException;
import com.taskflow.securetaskflow.project.Project;
import com.taskflow.securetaskflow.project.ProjectService;
import com.taskflow.securetaskflow.team.TeamService;
import com.taskflow.securetaskflow.user.User;
import com.taskflow.securetaskflow.user.UserRepository;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service that coordinates task creation, updates, comments and audit events.
 */
@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskCommentRepository taskCommentRepository;
    private final ProjectService projectService;
    private final TeamService teamService;
    private final UserRepository userRepository;
    private final SecurityUtils securityUtils;
    private final AuditService auditService;

    public TaskService(TaskRepository taskRepository, TaskCommentRepository taskCommentRepository,
                       ProjectService projectService, TeamService teamService, UserRepository userRepository,
                       SecurityUtils securityUtils, AuditService auditService) {
        this.taskRepository = taskRepository;
        this.taskCommentRepository = taskCommentRepository;
        this.projectService = projectService;
        this.teamService = teamService;
        this.userRepository = userRepository;
        this.securityUtils = securityUtils;
        this.auditService = auditService;
    }

    @Transactional
    public TaskResponse create(CreateTaskRequest request) {
        User actor = securityUtils.currentUser();
        Project project = projectService.findProject(request.projectId());
        teamService.requireTeamMemberOrAdmin(project.getTeam(), actor);
        // Assignees are optional, but when provided they must already belong
        // to the project team so tasks cannot be assigned outside visibility.
        User assignee = resolveAssignee(request.assigneeId(), project);
        TaskItem saved = taskRepository.save(new TaskItem(
                request.title(),
                request.description(),
                request.priority(),
                request.dueDate(),
                project,
                assignee,
                actor
        ));
        auditService.record("TASK_CREATED", "TASK", saved.getId(), "Task created: " + saved.getTitle(), actor);
        return TaskResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public Page<TaskResponse> listByProject(Long projectId, Pageable pageable) {
        User actor = securityUtils.currentUser();
        Project project = projectService.findProject(projectId);
        teamService.requireTeamMemberOrAdmin(project.getTeam(), actor);
        return taskRepository.findByProject_Id(projectId, pageable).map(TaskResponse::from);
    }

    @Transactional(readOnly = true)
    public TaskResponse get(Long taskId) {
        User actor = securityUtils.currentUser();
        TaskItem task = findTask(taskId);
        teamService.requireTeamMemberOrAdmin(task.getProject().getTeam(), actor);
        return TaskResponse.from(task);
    }

    @Transactional
    public TaskResponse update(Long taskId, UpdateTaskRequest request) {
        User actor = securityUtils.currentUser();
        TaskItem task = findTask(taskId);
        teamService.requireTeamMemberOrAdmin(task.getProject().getTeam(), actor);
        User assignee = resolveAssignee(request.assigneeId(), task.getProject());
        task.update(request.title(), request.description(), request.priority(), request.dueDate(), assignee);
        TaskItem saved = taskRepository.save(task);
        auditService.record("TASK_UPDATED", "TASK", saved.getId(), "Task details updated", actor);
        return TaskResponse.from(saved);
    }

    @Transactional
    public TaskResponse changeStatus(Long taskId, ChangeTaskStatusRequest request) {
        User actor = securityUtils.currentUser();
        TaskItem task = findTask(taskId);
        teamService.requireTeamMemberOrAdmin(task.getProject().getTeam(), actor);
        // Keep the previous state for the audit message. That makes workflow
        // history readable without diffing two task snapshots.
        TaskStatus previous = task.getStatus();
        task.changeStatus(request.status());
        TaskItem saved = taskRepository.save(task);
        auditService.record(
                "TASK_STATUS_CHANGED",
                "TASK",
                saved.getId(),
                "Status changed from " + previous + " to " + request.status(),
                actor
        );
        return TaskResponse.from(saved);
    }

    @Transactional
    public TaskCommentResponse addComment(Long taskId, CreateCommentRequest request) {
        User actor = securityUtils.currentUser();
        TaskItem task = findTask(taskId);
        teamService.requireTeamMemberOrAdmin(task.getProject().getTeam(), actor);
        TaskComment saved = taskCommentRepository.save(new TaskComment(request.body(), task, actor));
        auditService.record("TASK_COMMENT_ADDED", "TASK", task.getId(), "Comment added", actor);
        return TaskCommentResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public List<TaskCommentResponse> listComments(Long taskId) {
        User actor = securityUtils.currentUser();
        TaskItem task = findTask(taskId);
        teamService.requireTeamMemberOrAdmin(task.getProject().getTeam(), actor);
        return taskCommentRepository.findByTask_IdOrderByCreatedAtAsc(taskId).stream()
                .map(TaskCommentResponse::from)
                .toList();
    }

    private TaskItem findTask(Long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
    }

    private User resolveAssignee(Long assigneeId, Project project) {
        if (assigneeId == null) {
            return null;
        }
        User assignee = userRepository.findById(assigneeId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignee not found"));
        // Membership validation is centralized here because both create and
        // update paths need the same assignment rule.
        if (!project.getTeam().hasMember(assignee)) {
            throw new BadRequestException("Assignee must be a member of the project team");
        }
        return assignee;
    }
}
