package com.tacticboard.persistence.service.impl;

import com.tacticboard.core.exception.ResourceNotFoundException;
import com.tacticboard.core.model.entity.training.Attendance;
import com.tacticboard.core.service.StatsService;
import com.tacticboard.persistence.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StatsServiceImpl implements StatsService {

    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;
    private final TrainingSessionRepository trainingSessionRepository;
    private final AttendanceRepository attendanceRepository;
    private final TacticRepository tacticRepository;
    private final VideoRepository videoRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final TrainingPlanRepository trainingPlanRepository;

    @Autowired
    public StatsServiceImpl(
            UserRepository userRepository,
            TeamRepository teamRepository,
            PlayerRepository playerRepository,
            TrainingSessionRepository trainingSessionRepository,
            AttendanceRepository attendanceRepository,
            TacticRepository tacticRepository,
            VideoRepository videoRepository,
            SubscriptionRepository subscriptionRepository,
            TrainingPlanRepository trainingPlanRepository) {
        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
        this.playerRepository = playerRepository;
        this.trainingSessionRepository = trainingSessionRepository;
        this.attendanceRepository = attendanceRepository;
        this.tacticRepository = tacticRepository;
        this.videoRepository = videoRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.trainingPlanRepository = trainingPlanRepository;
    }

    @Override
    public Map<String, Object> getUserStats(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }

        Map<String, Object> stats = new HashMap<>();

        // Count teams created by user
        int teamCount = teamRepository.countByOwnerId(userId);
        stats.put("teamCount", teamCount);

        // Count tactics created by user's teams
        List<Long> teamIds = teamRepository.findByOwnerId(userId).stream()
                .map(team -> team.getId())
                .collect(Collectors.toList());

        int tacticCount = 0;
        int videoCount = 0;

        for (Long teamId : teamIds) {
            tacticCount += tacticRepository.findByTeamId(teamId).size();
            videoCount += videoRepository.findByTeamId(teamId).size();
        }

        stats.put("tacticCount", tacticCount);
        stats.put("videoCount", videoCount);

        // Get subscription info
        subscriptionRepository.findByUserIdAndIsActiveTrue(userId).ifPresent(subscription -> {
            stats.put("subscriptionPlan", subscription.getPlanType().name());
            stats.put("subscriptionEndDate", subscription.getEndDate().toString());
            stats.put("subscriptionDaysLeft", ChronoUnit.DAYS.between(LocalDate.now(), subscription.getEndDate()));
        });

        return stats;
    }

    @Override
    public Map<String, Object> getTeamStats(Long teamId) {
        if (!teamRepository.existsById(teamId)) {
            throw new ResourceNotFoundException("Team", "id", teamId);
        }

        Map<String, Object> stats = new HashMap<>();

        // Count players in team
        int playerCount = playerRepository.countByTeamId(teamId);
        stats.put("playerCount", playerCount);

        // Count tactics for team
        int tacticCount = tacticRepository.findByTeamId(teamId).size();
        stats.put("tacticCount", tacticCount);

        // Count videos for team
        int videoCount = videoRepository.findByTeamId(teamId).size();
        stats.put("videoCount", videoCount);

        // Count training sessions
        int trainingSessionCount = 0;
        List<Long> trainingPlanIds = trainingPlanRepository.findByTeamId(teamId).stream()
                .map(plan -> plan.getId())
                .collect(Collectors.toList());

        for (Long planId : trainingPlanIds) {
            trainingSessionCount += trainingSessionRepository.findByTrainingPlanId(planId).size();
        }

        stats.put("trainingSessionCount", trainingSessionCount);

        return stats;
    }

    @Override
    public Map<String, Object> getPlayerStats(Long playerId) {
        if (!playerRepository.existsById(playerId)) {
            throw new ResourceNotFoundException("Player", "id", playerId);
        }

        Map<String, Object> stats = new HashMap<>();

        // Get attendance stats
        List<Attendance> attendances = attendanceRepository.findByPlayerId(playerId);

        int totalSessions = attendances.size();
        long presentCount = attendances.stream()
                .filter(a -> a.getStatus() == Attendance.AttendanceStatus.PRESENT)
                .count();
        long absentCount = attendances.stream()
                .filter(a -> a.getStatus() == Attendance.AttendanceStatus.ABSENT)
                .count();
        long lateCount = attendances.stream()
                .filter(a -> a.getStatus() == Attendance.AttendanceStatus.LATE)
                .count();

        stats.put("totalSessions", totalSessions);
        stats.put("presentCount", presentCount);
        stats.put("absentCount", absentCount);
        stats.put("lateCount", lateCount);

        if (totalSessions > 0) {
            stats.put("attendanceRate", (double) presentCount / totalSessions * 100);
        } else {
            stats.put("attendanceRate", 0.0);
        }

        return stats;
    }

    @Override
    public Map<String, Object> getAttendanceStats(Long teamId, LocalDate startDate, LocalDate endDate) {
        if (!teamRepository.existsById(teamId)) {
            throw new ResourceNotFoundException("Team", "id", teamId);
        }

        Map<String, Object> stats = new HashMap<>();

        // Get all players in team
        List<Long> playerIds = playerRepository.findByTeamId(teamId).stream()
                .map(player -> player.getId())
                .collect(Collectors.toList());

        // Get all training sessions in date range
        List<Long> trainingPlanIds = trainingPlanRepository.findByTeamId(teamId).stream()
                .map(plan -> plan.getId())
                .collect(Collectors.toList());

        Map<Long, Map<String, Object>> playerAttendance = new HashMap<>();

        for (Long playerId : playerIds) {
            Map<String, Object> playerStats = new HashMap<>();
            int totalSessions = 0;
            int presentCount = 0;
            int absentCount = 0;
            int lateCount = 0;

            for (Long planId : trainingPlanIds) {
                List<Long> sessionIds = trainingSessionRepository.findByTrainingPlanId(planId).stream()
                        .filter(session -> !session.getSessionDate().toLocalDate().isBefore(startDate) &&
                                !session.getSessionDate().toLocalDate().isAfter(endDate))
                        .map(session -> session.getId())
                        .collect(Collectors.toList());

                totalSessions += sessionIds.size();

                for (Long sessionId : sessionIds) {
                    attendanceRepository.findByPlayerIdAndTrainingSessionId(playerId, sessionId)
                            .ifPresent(attendance -> {
                                switch (attendance.getStatus()) {
                                    case PRESENT:
                                        playerStats.put("presentCount",
                                                (int) playerStats.getOrDefault("presentCount", 0) + 1);
                                        break;
                                    case ABSENT:
                                        playerStats.put("absentCount",
                                                (int) playerStats.getOrDefault("absentCount", 0) + 1);
                                        break;
                                    case LATE:
                                        playerStats.put("lateCount",
                                                (int) playerStats.getOrDefault("lateCount", 0) + 1);
                                        break;
                                }
                            });
                }
            }

            playerStats.put("totalSessions", totalSessions);
            playerStats.put("presentCount", playerStats.getOrDefault("presentCount", 0));
            playerStats.put("absentCount", playerStats.getOrDefault("absentCount", 0));
            playerStats.put("lateCount", playerStats.getOrDefault("lateCount", 0));

            if (totalSessions > 0) {
                playerStats.put("attendanceRate",
                        (double) (int) playerStats.getOrDefault("presentCount", 0) / totalSessions * 100);
            } else {
                playerStats.put("attendanceRate", 0.0);
            }

            playerAttendance.put(playerId, playerStats);
        }

        stats.put("playerAttendance", playerAttendance);

        // Overall team attendance
        int totalTeamSessions = 0;
        int totalPresent = 0;
        int totalAbsent = 0;
        int totalLate = 0;

        for (Map<String, Object> playerStats : playerAttendance.values()) {
            totalTeamSessions += (int) playerStats.get("totalSessions");
            totalPresent += (int) playerStats.get("presentCount");
            totalAbsent += (int) playerStats.get("absentCount");
            totalLate += (int) playerStats.get("lateCount");
        }

        stats.put("totalTeamSessions", totalTeamSessions);
        stats.put("totalPresent", totalPresent);
        stats.put("totalAbsent", totalAbsent);
        stats.put("totalLate", totalLate);

        if (totalTeamSessions > 0) {
            stats.put("teamAttendanceRate", (double) totalPresent / totalTeamSessions * 100);
        } else {
            stats.put("teamAttendanceRate", 0.0);
        }

        return stats;
    }

    @Override
    public Map<String, Object> getSystemStats() {
        Map<String, Object> stats = new HashMap<>();

        // Count total users
        long userCount = userRepository.count();
        stats.put("userCount", userCount);

        // Count total teams
        long teamCount = teamRepository.count();
        stats.put("teamCount", teamCount);

        // Count total players
        long playerCount = playerRepository.count();
        stats.put("playerCount", playerCount);

        // Count total tactics
        long tacticCount = tacticRepository.count();
        stats.put("tacticCount", tacticCount);

        // Count total videos
        long videoCount = videoRepository.count();
        stats.put("videoCount", videoCount);

        // Count active subscriptions
        long activeSubscriptionCount = subscriptionRepository.countByIsActiveTrue();
        stats.put("activeSubscriptionCount", activeSubscriptionCount);

        // Count subscriptions by plan type
        Map<String, Long> subscriptionsByPlan = new HashMap<>();
        for (String planType : subscriptionRepository.findDistinctPlanTypes()) {
            long count = subscriptionRepository.countByPlanTypeAndIsActiveTrue(planType);
            subscriptionsByPlan.put(planType, count);
        }
        stats.put("subscriptionsByPlan", subscriptionsByPlan);

        return stats;
    }

    @Override
    public Map<String, Object> getTrainingStats(Long teamId, LocalDate startDate, LocalDate endDate) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getTrainingStats'");
    }
}