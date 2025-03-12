package com.tacticboard.persistence.repository;

import com.tacticboard.core.model.entity.training.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    
    List<Attendance> findByTrainingSessionId(Long trainingSessionId);
    
    List<Attendance> findByPlayerId(Long playerId);
    
    Optional<Attendance> findByPlayerIdAndTrainingSessionId(Long playerId, Long trainingSessionId);
}