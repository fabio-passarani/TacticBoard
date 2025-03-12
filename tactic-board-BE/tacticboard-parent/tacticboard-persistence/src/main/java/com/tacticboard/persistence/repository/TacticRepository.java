package com.tacticboard.persistence.repository;

import com.tacticboard.core.model.entity.tactic.Tactic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TacticRepository extends JpaRepository<Tactic, Long> {
    
    List<Tactic> findByTeamId(Long teamId);
    
    List<Tactic> findByIsPublicTrue();
}