package com.tacticboard.api.controller;

import com.tacticboard.api.payload.request.TeamRequest;
import com.tacticboard.api.util.ResponseBuilder;
import com.tacticboard.core.exception.ResourceNotFoundException;
import com.tacticboard.core.model.entity.team.Team;
import com.tacticboard.core.model.entity.user.User;
import com.tacticboard.core.service.TeamService;
import com.tacticboard.core.service.UserService;
import com.tacticboard.security.util.SecurityUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teams")
public class TeamController {

    private final TeamService teamService;
    private final UserService userService;

    @Autowired
    public TeamController(TeamService teamService, UserService userService) {
        this.teamService = teamService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<?> getAllTeams() {
        String username = SecurityUtils.getCurrentUsername()
                .orElseThrow(() -> new IllegalStateException("No authenticated user found"));
        
        User user = userService.getUserByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        List<Team> teams = teamService.getTeamsByUser(user.getId());
        return ResponseBuilder.success(teams);
    }

    @GetMapping("/{id}")
    @PreAuthorize("@teamSecurityService.canAccessTeam(#id)")
    public ResponseEntity<?> getTeamById(@PathVariable Long id) {
        Team team = teamService.getTeamById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team", "id", id));
        
        return ResponseBuilder.success(team);
    }

    @PostMapping
    public ResponseEntity<?> createTeam(@Valid @RequestBody TeamRequest teamRequest) {
        String username = SecurityUtils.getCurrentUsername()
                .orElseThrow(() -> new IllegalStateException("No authenticated user found"));
        
        User user = userService.getUserByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        Team team = new Team();
        team.setName(teamRequest.getName());
        team.setDescription(teamRequest.getDescription());
        team.setCategory(teamRequest.getCategory());
        team.setOwner(user);
        
        Team createdTeam = teamService.createTeam(team);
        return ResponseBuilder.created(createdTeam);
    }

    @PutMapping("/{id}")
    @PreAuthorize("@teamSecurityService.isTeamOwner(#id)")
    public ResponseEntity<?> updateTeam(@PathVariable Long id, @Valid @RequestBody TeamRequest teamRequest) {
        Team team = teamService.getTeamById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team", "id", id));
        
        team.setName(teamRequest.getName());
        team.setDescription(teamRequest.getDescription());
        team.setCategory(teamRequest.getCategory());
        
        Team updatedTeam = teamService.updateTeam(team);
        return ResponseBuilder.success("Team updated successfully", updatedTeam);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@teamSecurityService.isTeamOwner(#id)")
    public ResponseEntity<?> deleteTeam(@PathVariable Long id) {
        teamService.deleteTeam(id);
        return ResponseBuilder.success("Team deleted successfully", null);
    }

    @PostMapping("/{teamId}/members/{userId}")
    @PreAuthorize("@teamSecurityService.isTeamOwner(#teamId)")
    public ResponseEntity<?> addMember(@PathVariable Long teamId, @PathVariable Long userId) {
        teamService.addMember(teamId, userId);
        return ResponseBuilder.success("Member added successfully", null);
    }

    @DeleteMapping("/{teamId}/members/{userId}")
    @PreAuthorize("@teamSecurityService.isTeamOwner(#teamId)")
    public ResponseEntity<?> removeMember(@PathVariable Long teamId, @PathVariable Long userId) {
        teamService.removeMember(teamId, userId);
        return ResponseBuilder.success("Member removed successfully", null);
    }
}