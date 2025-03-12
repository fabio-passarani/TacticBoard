package com.tacticboard.persistence.repository;

import com.tacticboard.core.model.entity.training.TrainingPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrainingPlanRepository extends JpaRepository<TrainingPlan, Long> {
    
    List<TrainingPlan> findByTeamId(Long teamId);
    
    List<TrainingPlan> findByTeamIdAndIsActiveTrue(Long teamId);
}