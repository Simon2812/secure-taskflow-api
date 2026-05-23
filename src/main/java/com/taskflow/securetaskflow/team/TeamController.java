package com.taskflow.securetaskflow.team;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for team management endpoints.
 */
@RestController
@RequestMapping("/api/teams")
public class TeamController {

    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TeamResponse create(@Valid @RequestBody CreateTeamRequest request) {
        return teamService.create(request);
    }

    @GetMapping
    public List<TeamResponse> listMyTeams() {
        return teamService.listMyTeams();
    }

    @GetMapping("/{teamId}")
    public TeamResponse get(@PathVariable Long teamId) {
        return teamService.get(teamId);
    }

    @PutMapping("/{teamId}")
    public TeamResponse update(@PathVariable Long teamId, @Valid @RequestBody UpdateTeamRequest request) {
        return teamService.update(teamId, request);
    }

    @PostMapping("/{teamId}/members")
    public TeamResponse addMember(@PathVariable Long teamId, @Valid @RequestBody AddTeamMemberRequest request) {
        return teamService.addMember(teamId, request);
    }

    @DeleteMapping("/{teamId}/members/{userId}")
    public TeamResponse removeMember(@PathVariable Long teamId, @PathVariable Long userId) {
        return teamService.removeMember(teamId, userId);
    }
}
