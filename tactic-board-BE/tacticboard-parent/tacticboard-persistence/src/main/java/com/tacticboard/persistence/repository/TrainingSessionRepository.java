package com.tacticboard.persistence.repository;

import com.tacticboard.core.model.entity.training.TrainingSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TrainingSessionRepository extends JpaRepository<TrainingSession, Long> {
    
    List<TrainingSession> findByTrainingPlanId(Long trainingPlanId);
    
    List<TrainingSession> findBySessionDateBetween(LocalDateTime start, LocalDateTime end);
    
    List<TrainingSession> findByTrainingPlanIdAndSessionDateBetween(Long trainingPlanId, LocalDateTime start, LocalDateTime end);
}