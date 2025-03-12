package com.tacticboard.persistence.repository;

import com.tacticboard.core.model.entity.team.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
    
    List<Team> findByOwnerId(Long ownerId);
    
    int countByOwnerId(Long ownerId);
}