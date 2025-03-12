package com.tacticboard.persistence.service.impl;

import com.tacticboard.core.exception.ResourceNotFoundException;
import com.tacticboard.core.model.entity.team.Team;
import com.tacticboard.core.model.entity.team.Player;
import com.tacticboard.core.model.entity.team.TeamMember;
import com.tacticboard.core.service.TeamService;
import com.tacticboard.persistence.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TeamServiceImpl implements TeamService {

    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final UserRepository userRepository;
    private final TacticRepository tacticRepository;
    private final VideoRepository videoRepository;
    private final TrainingPlanRepository trainingPlanRepository;

    @Autowired
    public TeamServiceImpl(
            TeamRepository teamRepository,
            PlayerRepository playerRepository,
            TeamMemberRepository teamMemberRepository,
            UserRepository userRepository,
            TacticRepository tacticRepository,
            VideoRepository videoRepository,
            TrainingPlanRepository trainingPlanRepository) {
        this.teamRepository = teamRepository;
        this.playerRepository = playerRepository;
        this.teamMemberRepository = teamMemberRepository;
        this.userRepository = userRepository;
        this.tacticRepository = tacticRepository;
        this.videoRepository = videoRepository;
        this.trainingPlanRepository = trainingPlanRepository;
    }

    @Override
    public Team createTeam(Team team) {
        if (!userRepository.existsById(team.getOwner().getId())) {
            throw new ResourceNotFoundException("User", "id", team.getOwner().getId());
        }
        
        team.setCreatedAt(LocalDateTime.now());
        team.setUpdatedAt(LocalDateTime.now());
        
        return teamRepository.save(team);
    }

    @Override
    public Team updateTeam(Team team) {
        if (!teamRepository.existsById(team.getId())) {
            throw new ResourceNotFoundException("Team", "id", team.getId());
        }
        
        team.setUpdatedAt(LocalDateTime.now());
        return teamRepository.save(team);
    }

    @Override
    public Optional<Team> getTeamById(Long id) {
        return teamRepository.findById(id);
    }

    @Override
    public List<Team> getTeamsByOwnerId(Long ownerId) {
        if (!userRepository.existsById(ownerId)) {
            throw new ResourceNotFoundException("User", "id", ownerId);
        }
        
        return teamRepository.findByOwnerId(ownerId);
    }

    @Override
    public List<Team> getTeamsByMemberId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }
        
        return teamRepository.findByTeamMembersUserId(userId);
    }

    @Override
    public void deleteTeam(Long id) {
        if (!teamRepository.existsById(id)) {
            throw new ResourceNotFoundException("Team", "id", id);
        }
        
        // Delete all related entities
        tacticRepository.deleteByTeamId(id);
        videoRepository.deleteByTeamId(id);
        trainingPlanRepository.deleteByTeamId(id);
        playerRepository.deleteByTeamId(id);
        teamMemberRepository.deleteByTeamId(id);
        
        teamRepository.deleteById(id);
    }

    @Override
    public Player createPlayer(Player player, Long teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team", "id", teamId));
        
        player.setTeam(team);
        player.setCreatedAt(LocalDateTime.now());
        
        return playerRepository.save(player);
    }

    @Override
    public Player updatePlayer(Player player) {
        if (!playerRepository.existsById(player.getId())) {
            throw new ResourceNotFoundException("Player", "id", player.getId());
        }
        
        player.setUpdatedAt(LocalDateTime.now());
        return playerRepository.save(player);
    }

    @Override
    public Optional<Player> getPlayerById(Long id) {
        return playerRepository.findById(id);
    }

    @Override
    public List<Player> getPlayersByTeamId(Long teamId) {
        if (!teamRepository.existsById(teamId)) {
            throw new ResourceNotFoundException("Team", "id", teamId);
        }
        
        return playerRepository.findByTeamId(teamId);
    }

    @Override
    public void deletePlayer(Long id) {
        if (!playerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Player", "id", id);
        }
        
        // Delete all related entities like attendance records
        // This would be handled by the AttendanceRepository
        
        playerRepository.deleteById(id);
    }

    @Override
    public TeamMember addTeamMember(TeamMember teamMember, Long teamId, Long userId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team", "id", teamId));
        
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }
        
        // Check if user is already a member
        if (teamMemberRepository.existsByTeamIdAndUserId(teamId, userId)) {
            throw new IllegalStateException("User is already a member of this team");
        }
        
        teamMember.setTeam(team);
        teamMember.getUser().setId(userId);
        teamMember.setJoinedAt(LocalDateTime.now());
        
        return teamMemberRepository.save(teamMember);
    }

    @Override
    public TeamMember updateTeamMember(TeamMember teamMember) {
        if (!teamMemberRepository.existsById(teamMember.getId())) {
            throw new ResourceNotFoundException("TeamMember", "id", teamMember.getId());
        }
        
        return teamMemberRepository.save(teamMember);
    }

    @Override
    public Optional<TeamMember> getTeamMemberById(Long id) {
        return teamMemberRepository.findById(id);
    }

    @Override
    public List<TeamMember> getTeamMembersByTeamId(Long teamId) {
        if (!teamRepository.existsById(teamId)) {
            throw new ResourceNotFoundException("Team", "id", teamId);
        }
        
        return teamMemberRepository.findByTeamId(teamId);
    }

    @Override
    public Optional<TeamMember> getTeamMemberByTeamAndUser(Long teamId, Long userId) {
        if (!teamRepository.existsById(teamId)) {
            throw new ResourceNotFoundException("Team", "id", teamId);
        }
        
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }
        
        return teamMemberRepository.findByTeamIdAndUserId(teamId, userId);
    }

    @Override
    public void removeTeamMember(Long id) {
        if (!teamMemberRepository.existsById(id)) {
            throw new ResourceNotFoundException("TeamMember", "id", id);
        }
        
        teamMemberRepository.deleteById(id);
    }

    @Override
    public List<Team> searchTeams(String query, Long ownerId) {
        if (ownerId != null) {
            if (!userRepository.existsById(ownerId)) {
                throw new ResourceNotFoundException("User", "id", ownerId);
            }
            return teamRepository.findByNameContainingAndOwnerId(query, ownerId);
        } else {
            return teamRepository.findByNameContaining(query);
        }
    }

    @Override
    public List<Player> searchPlayers(String query, Long teamId) {
        if (teamId != null) {
            if (!teamRepository.existsById(teamId)) {
                throw new ResourceNotFoundException("Team", "id", teamId);
            }
            return playerRepository.findByNameContainingAndTeamId(query, teamId);
        } else {
            return playerRepository.findByNameContaining(query);
        }
    }

    @Override
    public boolean isUserTeamMember(Long teamId, Long userId) {
        if (!teamRepository.existsById(teamId)) {
            throw new ResourceNotFoundException("Team", "id", teamId);
        }
        
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }
        
        return teamMemberRepository.existsByTeamIdAndUserId(teamId, userId);
    }

    @Override
    public boolean isUserTeamOwner(Long teamId, Long userId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team", "id", teamId));
        
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }
        
        return team.getOwner().getId().equals(userId);
    }

    @Override
    public void transferTeamOwnership(Long teamId, Long newOwnerId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team", "id", teamId));
        
        if (!userRepository.existsById(newOwnerId)) {
            throw new ResourceNotFoundException("User", "id", newOwnerId);
        }
        
        // Set the new owner
        team.getOwner().setId(newOwnerId);
        team.setUpdatedAt(LocalDateTime.now());
        
        teamRepository.save(team);
    }
}