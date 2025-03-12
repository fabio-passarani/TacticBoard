package com.tacticboard.core.service;

import java.time.LocalDate;
import java.util.Map;

public interface StatsService {
    
    Map<String, Object> getUserStats(Long userId);
    
    Map<String, Object> getTeamStats(Long teamId);
    
    Map<String, Object> getPlayerStats(Long playerId);
    
    Map<String, Object> getAttendanceStats(Long teamId, LocalDate startDate, LocalDate endDate);
    
    Map<String, Object> getTrainingStats(Long teamId, LocalDate startDate, LocalDate endDate);
    
    Map<String, Object> getSystemStats();
}