package com.tacticboard.core.service;

import com.tacticboard.core.model.entity.training.Attendance;
import com.tacticboard.core.model.entity.training.TrainingPlan;
import com.tacticboard.core.model.entity.training.TrainingSession;

import java.util.List;
import java.util.Optional;

public interface TrainingService {
    
    TrainingPlan createTrainingPlan(TrainingPlan trainingPlan);
    
    TrainingPlan updateTrainingPlan(TrainingPlan trainingPlan);
    
    Optional<TrainingPlan> getTrainingPlanById(Long id);
    
    List<TrainingPlan> getAllTrainingPlansByTeamId(Long teamId);
    
    void deleteTrainingPlan(Long id);
    
    TrainingSession createTrainingSession(TrainingSession session, Long planId);
    
    TrainingSession updateTrainingSession(TrainingSession session);
    
    Optional<TrainingSession> getTrainingSessionById(Long id);
    
    List<TrainingSession> getAllSessionsByPlanId(Long planId);
    
    void deleteTrainingSession(Long id);
    
    Attendance recordAttendance(Attendance attendance);
    
    Attendance updateAttendance(Attendance attendance);
    
    List<Attendance> getAttendanceBySessionId(Long sessionId);
    
    List<Attendance> getAttendanceByPlayerId(Long playerId);
}