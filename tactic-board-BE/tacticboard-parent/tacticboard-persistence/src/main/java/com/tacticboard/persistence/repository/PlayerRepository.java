package com.tacticboard.persistence.repository;

import com.tacticboard.core.model.entity.team.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {
    
    List<Player> findByTeamId(Long teamId);
    
    int countByTeamId(Long teamId);
}