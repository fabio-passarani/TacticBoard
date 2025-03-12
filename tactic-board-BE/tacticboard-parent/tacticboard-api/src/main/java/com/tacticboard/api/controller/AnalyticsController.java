package com.tacticboard.api.controller;

import com.tacticboard.api.util.ResponseBuilder;
import com.tacticboard.core.exception.ResourceNotFoundException;
import com.tacticboard.core.model.dto.AnalyticsDTO;
import com.tacticboard.core.model.dto.TeamPerformanceDTO;
import com.tacticboard.core.model.dto.UserActivityDTO;
import com.tacticboard.core.service.AnalyticsService;
import com.tacticboard.core.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;
    private final TeamService teamService;

    @Autowired
    public AnalyticsController(AnalyticsService analyticsService, TeamService teamService) {
        this.analyticsService = analyticsService;
        this.teamService = teamService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboardAnalytics() {
        AnalyticsDTO analytics = analyticsService.getDashboardAnalytics();
        return ResponseBuilder.success(analytics);
    }

    @GetMapping("/team/{teamId}")
    @PreAuthorize("@teamSecurityService.canAccessTeam(#teamId)")
    public ResponseEntity<?> getTeamAnalytics(@PathVariable Long teamId) {
        teamService.getTeamById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team", "id", teamId));
        
        TeamPerformanceDTO teamPerformance = analyticsService.getTeamPerformance(teamId);
        return ResponseBuilder.success(teamPerformance);
    }

    @GetMapping("/team/{teamId}/period")
    @PreAuthorize("@teamSecurityService.canAccessTeam(#teamId)")
    public ResponseEntity<?> getTeamAnalyticsByPeriod(
            @PathVariable Long teamId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        teamService.getTeamById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team", "id", teamId));
        
        TeamPerformanceDTO teamPerformance = analyticsService.getTeamPerformanceByPeriod(teamId, startDate, endDate);
        return ResponseBuilder.success(teamPerformance);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isCurrentUser(#userId)")
    public ResponseEntity<?> getUserActivity(@PathVariable Long userId) {
        UserActivityDTO userActivity = analyticsService.getUserActivity(userId);
        return ResponseBuilder.success(userActivity);
    }

    @GetMapping("/popular/tactics")
    public ResponseEntity<?> getPopularTactics() {
        List<?> popularTactics = analyticsService.getPopularTactics();
        return ResponseBuilder.success(popularTactics);
    }

    @GetMapping("/popular/exercises")
    public ResponseEntity<?> getPopularExercises() {
        List<?> popularExercises = analyticsService.getPopularExercises();
        return ResponseBuilder.success(popularExercises);
    }

    @GetMapping("/usage/summary")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getUsageSummary() {
        Object usageSummary = analyticsService.getUsageSummary();
        return ResponseBuilder.success(usageSummary);
    }

    @GetMapping("/usage/detail")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getDetailedUsage(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        Object detailedUsage = analyticsService.getDetailedUsage(startDate, endDate);
        return ResponseBuilder.success(detailedUsage);
    }
}