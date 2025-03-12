package com.tacticboard.core.service;

import com.tacticboard.core.model.entity.team.Player;
import com.tacticboard.core.model.entity.team.Team;

import java.util.List;
import java.util.Optional;

public interface TeamService {
    
    Team createTeam(Team team);
    
    Team updateTeam(Team team);
    
    Optional<Team> getTeamById(Long id);
    
    List<Team> getAllTeamsByUserId(Long userId);
    
    void deleteTeam(Long id);
    
    Player addPlayerToTeam(Player player, Long teamId);
    
    Player updatePlayer(Player player);
    
    Optional<Player> getPlayerById(Long id);
    
    List<Player> getAllPlayersByTeamId(Long teamId);
    
    void deletePlayer(Long id);
}