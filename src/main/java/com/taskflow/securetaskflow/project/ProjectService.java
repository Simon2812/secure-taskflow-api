package com.taskflow.securetaskflow.project;

import com.taskflow.securetaskflow.audit.AuditService;
import com.taskflow.securetaskflow.common.SecurityUtils;
import com.taskflow.securetaskflow.common.ResourceNotFoundException;
import com.taskflow.securetaskflow.team.Team;
import com.taskflow.securetaskflow.team.TeamService;
import com.taskflow.securetaskflow.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service that enforces project ownership, membership and lifecycle rules.
 */
@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final TeamService teamService;
    private final SecurityUtils securityUtils;
    private final AuditService auditService;

    public ProjectService(ProjectRepository projectRepository, TeamService teamService,
                          SecurityUtils securityUtils, AuditService auditService) {
        this.projectRepository = projectRepository;
        this.teamService = teamService;
        this.securityUtils = securityUtils;
        this.auditService = auditService;
    }

    @Transactional
    public ProjectResponse create(CreateProjectRequest request) {
        User actor = securityUtils.currentUser();
        Team team = teamService.findTeam(request.teamId());
        // Project creation is limited to team owners/managers/admins because
        // projects define the work boundary for all team tasks.
        teamService.requireManagerAccess(team, actor);
        Project saved = projectRepository.save(
                new Project(request.name(), request.description(), request.dueDate(), team, actor)
        );
        auditService.record("PROJECT_CREATED", "PROJECT", saved.getId(), "Project created: " + saved.getName(), actor);
        return ProjectResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public Page<ProjectResponse> list(Pageable pageable) {
        User actor = securityUtils.currentUser();
        // List only projects connected to the caller's team memberships; admin
        // broad visibility can be added later without changing the repository.
        return projectRepository.findByTeam_Members_Id(actor.getId(), pageable).map(ProjectResponse::from);
    }

    @Transactional(readOnly = true)
    public ProjectResponse get(Long projectId) {
        User actor = securityUtils.currentUser();
        Project project = findProject(projectId);
        teamService.requireTeamMemberOrAdmin(project.getTeam(), actor);
        return ProjectResponse.from(project);
    }

    @Transactional
    public ProjectResponse update(Long projectId, UpdateProjectRequest request) {
        User actor = securityUtils.currentUser();
        Project project = findProject(projectId);
        // Updating project metadata has the same access boundary as creating
        // the project.
        teamService.requireManagerAccess(project.getTeam(), actor);
        project.update(request.name(), request.description(), request.status(), request.dueDate());
        Project saved = projectRepository.save(project);
        auditService.record("PROJECT_UPDATED", "PROJECT", saved.getId(), "Project updated", actor);
        return ProjectResponse.from(saved);
    }

    public Project findProject(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
    }
}
