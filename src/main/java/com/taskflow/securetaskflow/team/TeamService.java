package com.taskflow.securetaskflow.team;

import com.taskflow.securetaskflow.audit.AuditService;
import com.taskflow.securetaskflow.common.BadRequestException;
import com.taskflow.securetaskflow.common.ForbiddenActionException;
import com.taskflow.securetaskflow.common.ResourceNotFoundException;
import com.taskflow.securetaskflow.common.SecurityUtils;
import com.taskflow.securetaskflow.user.Role;
import com.taskflow.securetaskflow.user.User;
import com.taskflow.securetaskflow.user.UserRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service that manages team ownership and membership operations.
 */
@Service
public class TeamService {

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final SecurityUtils securityUtils;
    private final AuditService auditService;

    public TeamService(TeamRepository teamRepository, UserRepository userRepository,
                       SecurityUtils securityUtils, AuditService auditService) {
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
        this.securityUtils = securityUtils;
        this.auditService = auditService;
    }

    @Transactional
    public TeamResponse create(CreateTeamRequest request) {
        User actor = securityUtils.currentUser();
        if (teamRepository.existsByName(request.name())) {
            throw new BadRequestException("Team name already exists");
        }
        // The creator becomes the owner and first member through the Team
        // constructor, so follow-up project/task actions work immediately.
        Team saved = teamRepository.save(new Team(request.name(), request.description(), actor));
        auditService.record("TEAM_CREATED", "TEAM", saved.getId(), "Team created: " + saved.getName(), actor);
        return TeamResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public List<TeamResponse> listMyTeams() {
        User actor = securityUtils.currentUser();
        return teamRepository.findByMembers_Id(actor.getId()).stream()
                .map(TeamResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public TeamResponse get(Long id) {
        User actor = securityUtils.currentUser();
        Team team = findTeam(id);
        requireTeamMemberOrAdmin(team, actor);
        return TeamResponse.from(team);
    }

    @Transactional
    public TeamResponse update(Long id, UpdateTeamRequest request) {
        User actor = securityUtils.currentUser();
        Team team = findTeam(id);
        requireManagerAccess(team, actor);
        team.update(request.name(), request.description());
        Team saved = teamRepository.save(team);
        auditService.record("TEAM_UPDATED", "TEAM", saved.getId(), "Team details updated", actor);
        return TeamResponse.from(saved);
    }

    @Transactional
    public TeamResponse addMember(Long teamId, AddTeamMemberRequest request) {
        User actor = securityUtils.currentUser();
        Team team = findTeam(teamId);
        requireManagerAccess(team, actor);
        // Resolve the user first so the response and audit event can show the
        // concrete account that was added to the team.
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        team.addMember(user);
        Team saved = teamRepository.save(team);
        auditService.record("TEAM_MEMBER_ADDED", "TEAM", saved.getId(), "Added user " + user.getEmail(), actor);
        return TeamResponse.from(saved);
    }

    @Transactional
    public TeamResponse removeMember(Long teamId, Long userId) {
        User actor = securityUtils.currentUser();
        Team team = findTeam(teamId);
        requireManagerAccess(team, actor);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (team.getOwner().getId().equals(user.getId())) {
            throw new BadRequestException("Team owner cannot be removed");
        }
        team.removeMember(user);
        Team saved = teamRepository.save(team);
        auditService.record("TEAM_MEMBER_REMOVED", "TEAM", saved.getId(), "Removed user " + user.getEmail(), actor);
        return TeamResponse.from(saved);
    }

    public Team findTeam(Long id) {
        return teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found"));
    }

    public void requireTeamMemberOrAdmin(Team team, User user) {
        // Admins can inspect or support any team; regular users must be linked
        // through membership to avoid leaking team/project data.
        if (user.getRole() == Role.ADMIN || team.hasMember(user)) {
            return;
        }
        throw new ForbiddenActionException("You are not a member of this team");
    }

    public void requireManagerAccess(Team team, User user) {
        boolean owner = team.getOwner().getId().equals(user.getId());
        boolean elevatedRole = user.getRole() == Role.ADMIN || user.getRole() == Role.MANAGER;
        // Ownership is enough for managing a team even if the owner has the
        // MEMBER role; elevated roles support cross-team administration.
        if (owner || elevatedRole) {
            return;
        }
        throw new ForbiddenActionException("Manager access is required");
    }
}
