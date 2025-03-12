package com.tacticboard.persistence.service.impl;

import com.tacticboard.core.exception.ResourceNotFoundException;
import com.tacticboard.core.model.entity.training.Attendance;
import com.tacticboard.core.model.entity.training.TrainingPlan;
import com.tacticboard.core.model.entity.training.TrainingSession;
import com.tacticboard.core.model.entity.training.Exercise;
import com.tacticboard.core.service.TrainingService;
import com.tacticboard.persistence.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TrainingServiceImpl implements TrainingService {

    private final TrainingPlanRepository trainingPlanRepository;
    private final TrainingSessionRepository trainingSessionRepository;
    private final ExerciseRepository exerciseRepository;
    private final AttendanceRepository attendanceRepository;
    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;

    @Autowired
    public TrainingServiceImpl(
            TrainingPlanRepository trainingPlanRepository,
            TrainingSessionRepository trainingSessionRepository,
            ExerciseRepository exerciseRepository,
            AttendanceRepository attendanceRepository,
            TeamRepository teamRepository,
            PlayerRepository playerRepository) {
        this.trainingPlanRepository = trainingPlanRepository;
        this.trainingSessionRepository = trainingSessionRepository;
        this.exerciseRepository = exerciseRepository;
        this.attendanceRepository = attendanceRepository;
        this.teamRepository = teamRepository;
        this.playerRepository = playerRepository;
    }

    @Override
    public TrainingPlan createTrainingPlan(TrainingPlan trainingPlan) {
        if (!teamRepository.existsById(trainingPlan.getTeam().getId())) {
            throw new ResourceNotFoundException("Team", "id", trainingPlan.getTeam().getId());
        }
        
        trainingPlan.setCreatedAt(LocalDateTime.now());
        trainingPlan.setUpdatedAt(LocalDateTime.now());
        
        return trainingPlanRepository.save(trainingPlan);
    }

    @Override
    public TrainingPlan updateTrainingPlan(TrainingPlan trainingPlan) {
        if (!trainingPlanRepository.existsById(trainingPlan.getId())) {
            throw new ResourceNotFoundException("TrainingPlan", "id", trainingPlan.getId());
        }
        
        trainingPlan.setUpdatedAt(LocalDateTime.now());
        return trainingPlanRepository.save(trainingPlan);
    }

    @Override
    public Optional<TrainingPlan> getTrainingPlanById(Long id) {
        return trainingPlanRepository.findById(id);
    }

    @Override
    public List<TrainingPlan> getTrainingPlansByTeamId(Long teamId) {
        if (!teamRepository.existsById(teamId)) {
            throw new ResourceNotFoundException("Team", "id", teamId);
        }
        
        return trainingPlanRepository.findByTeamId(teamId);
    }

    @Override
    public void deleteTrainingPlan(Long id) {
        if (!trainingPlanRepository.existsById(id)) {
            throw new ResourceNotFoundException("TrainingPlan", "id", id);
        }
        
        // Delete all sessions first
        List<TrainingSession> sessions = trainingSessionRepository.findByTrainingPlanId(id);
        for (TrainingSession session : sessions) {
            // Delete all attendances for each session
            attendanceRepository.deleteByTrainingSessionId(session.getId());
            
            // Delete all exercises for each session
            exerciseRepository.deleteByTrainingSessionId(session.getId());
        }
        
        trainingSessionRepository.deleteByTrainingPlanId(id);
        trainingPlanRepository.deleteById(id);
    }

    @Override
    public TrainingSession createTrainingSession(TrainingSession session, Long trainingPlanId) {
        TrainingPlan plan = trainingPlanRepository.findById(trainingPlanId)
                .orElseThrow(() -> new ResourceNotFoundException("TrainingPlan", "id", trainingPlanId));
        
        session.setTrainingPlan(plan);
        session.setCreatedAt(LocalDateTime.now());
        
        // Update the training plan's updated timestamp
        plan.setUpdatedAt(LocalDateTime.now());
        trainingPlanRepository.save(plan);
        
        return trainingSessionRepository.save(session);
    }

    @Override
    public TrainingSession updateTrainingSession(TrainingSession session) {
        if (!trainingSessionRepository.existsById(session.getId())) {
            throw new ResourceNotFoundException("TrainingSession", "id", session.getId());
        }
        
        return trainingSessionRepository.save(session);
    }

    @Override
    public Optional<TrainingSession> getTrainingSessionById(Long id) {
        return trainingSessionRepository.findById(id);
    }

    @Override
    public List<TrainingSession> getTrainingSessionsByPlanId(Long planId) {
        if (!trainingPlanRepository.existsById(planId)) {
            throw new ResourceNotFoundException("TrainingPlan", "id", planId);
        }
        
        return trainingSessionRepository.findByTrainingPlanId(planId);
    }

    @Override
    public void deleteTrainingSession(Long id) {
        if (!trainingSessionRepository.existsById(id)) {
            throw new ResourceNotFoundException("TrainingSession", "id", id);
        }
        
        // Delete all attendances and exercises first
        attendanceRepository.deleteByTrainingSessionId(id);
        exerciseRepository.deleteByTrainingSessionId(id);
        
        trainingSessionRepository.deleteById(id);
    }

    @Override
    public Exercise createExercise(Exercise exercise, Long sessionId) {
        TrainingSession session = trainingSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("TrainingSession", "id", sessionId));
        
        exercise.setTrainingSession(session);
        return exerciseRepository.save(exercise);
    }

    @Override
    public Exercise updateExercise(Exercise exercise) {
        if (!exerciseRepository.existsById(exercise.getId())) {
            throw new ResourceNotFoundException("Exercise", "id", exercise.getId());
        }
        
        return exerciseRepository.save(exercise);
    }

    @Override
    public Optional<Exercise> getExerciseById(Long id) {
        return exerciseRepository.findById(id);
    }

    @Override
    public List<Exercise> getExercisesBySessionId(Long sessionId) {
        if (!trainingSessionRepository.existsById(sessionId)) {
            throw new ResourceNotFoundException("TrainingSession", "id", sessionId);
        }
        
        return exerciseRepository.findByTrainingSessionId(sessionId);
    }

    @Override
    public void deleteExercise(Long id) {
        if (!exerciseRepository.existsById(id)) {
            throw new ResourceNotFoundException("Exercise", "id", id);
        }
        
        exerciseRepository.deleteById(id);
    }

    @Override
    public Attendance recordAttendance(Attendance attendance, Long sessionId, Long playerId) {
        TrainingSession session = trainingSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("TrainingSession", "id", sessionId));
        
        if (!playerRepository.existsById(playerId)) {
            throw new ResourceNotFoundException("Player", "id", playerId);
        }
        
        attendance.setTrainingSession(session);
        attendance.getPlayer().setId(playerId);
        attendance.setRecordedAt(LocalDateTime.now());
        
        return attendanceRepository.save(attendance);
    }

    @Override
    public Attendance updateAttendance(Attendance attendance) {
        if (!attendanceRepository.existsById(attendance.getId())) {
            throw new ResourceNotFoundException("Attendance", "id", attendance.getId());
        }
        
        attendance.setUpdatedAt(LocalDateTime.now());
        return attendanceRepository.save(attendance);
    }

    @Override
    public Optional<Attendance> getAttendanceById(Long id) {
        return attendanceRepository.findById(id);
    }

    @Override
    public List<Attendance> getAttendanceBySessionId(Long sessionId) {
        if (!trainingSessionRepository.existsById(sessionId)) {
            throw new ResourceNotFoundException("TrainingSession", "id", sessionId);
        }
        
        return attendanceRepository.findByTrainingSessionId(sessionId);
    }

    @Override
    public List<Attendance> getAttendanceByPlayerId(Long playerId) {
        if (!playerRepository.existsById(playerId)) {
            throw new ResourceNotFoundException("Player", "id", playerId);
        }
        
        return attendanceRepository.findByPlayerId(playerId);
    }

    @Override
    public Optional<Attendance> getAttendanceBySessionAndPlayer(Long sessionId, Long playerId) {
        if (!trainingSessionRepository.existsById(sessionId)) {
            throw new ResourceNotFoundException("TrainingSession", "id", sessionId);
        }
        
        if (!playerRepository.existsById(playerId)) {
            throw new ResourceNotFoundException("Player", "id", playerId);
        }
        
        return attendanceRepository.findByTrainingSessionIdAndPlayerId(sessionId, playerId);
    }

    @Override
    public void deleteAttendance(Long id) {
        if (!attendanceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Attendance", "id", id);
        }
        
        attendanceRepository.deleteById(id);
    }
}