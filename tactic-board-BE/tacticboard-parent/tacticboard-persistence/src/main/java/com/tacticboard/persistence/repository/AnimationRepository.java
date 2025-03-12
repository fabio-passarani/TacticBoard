package com.tacticboard.persistence.repository;

import com.tacticboard.core.model.entity.tactic.Animation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnimationRepository extends JpaRepository<Animation, Long> {
    
    List<Animation> findByTacticId(Long tacticId);
    
    List<Animation> findByTacticIdOrderBySequenceOrderAsc(Long tacticId);
}