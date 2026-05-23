package com.taskflow.securetaskflow.task;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for task workflow and collaboration endpoints.
 */
@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse create(@Valid @RequestBody CreateTaskRequest request) {
        return taskService.create(request);
    }

    @GetMapping
    public Page<TaskResponse> listByProject(@RequestParam Long projectId, Pageable pageable) {
        return taskService.listByProject(projectId, pageable);
    }

    @GetMapping("/{taskId}")
    public TaskResponse get(@PathVariable Long taskId) {
        return taskService.get(taskId);
    }

    @PutMapping("/{taskId}")
    public TaskResponse update(@PathVariable Long taskId, @Valid @RequestBody UpdateTaskRequest request) {
        return taskService.update(taskId, request);
    }

    @PatchMapping("/{taskId}/status")
    public TaskResponse changeStatus(@PathVariable Long taskId, @Valid @RequestBody ChangeTaskStatusRequest request) {
        return taskService.changeStatus(taskId, request);
    }

    @PostMapping("/{taskId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public TaskCommentResponse addComment(@PathVariable Long taskId, @Valid @RequestBody CreateCommentRequest request) {
        return taskService.addComment(taskId, request);
    }

    @GetMapping("/{taskId}/comments")
    public List<TaskCommentResponse> listComments(@PathVariable Long taskId) {
        return taskService.listComments(taskId);
    }
}
