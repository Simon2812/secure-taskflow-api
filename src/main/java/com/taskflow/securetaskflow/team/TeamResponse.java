package com.taskflow.securetaskflow.team;

import com.taskflow.securetaskflow.user.UserResponse;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;

/**
 * API response DTO for team data.
 */
public record TeamResponse(
        Long id,
        String name,
        String description,
        UserResponse owner,
        List<UserResponse> members,
        Instant createdAt
) {
    public static TeamResponse from(Team team) {
        return new TeamResponse(
                team.getId(),
                team.getName(),
                team.getDescription(),
                UserResponse.from(team.getOwner()),
                team.getMembers().stream()
                        .map(UserResponse::from)
                        .sorted(Comparator.comparing(UserResponse::id))
                        .toList(),
                team.getCreatedAt()
        );
    }
}
