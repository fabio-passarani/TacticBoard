package com.tacticboard.api.controller;

import com.tacticboard.api.util.ResponseBuilder;
import com.tacticboard.core.exception.ResourceNotFoundException;
import com.tacticboard.core.model.dto.TrainingDTO;
import com.tacticboard.core.model.entity.team.Team;
import com.tacticboard.core.service.TeamService;
import com.tacticboard.core.service.TrainingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trainings")
public class TrainingController {

    private final TrainingService trainingService;
    private final TeamService teamService;

    @Autowired
    public TrainingController(TrainingService trainingService, TeamService teamService) {
        this.trainingService = trainingService;
        this.teamService = teamService;
    }

    @GetMapping
    public ResponseEntity<?> getAllTrainings() {
        List<TrainingDTO> trainings = trainingService.getAllTrainings();
        return ResponseBuilder.success(trainings);
    }

    @GetMapping("/{id}")
    @PreAuthorize("@trainingSecurityService.canAccessTraining(#id)")
    public ResponseEntity<?> getTrainingById(@PathVariable Long id) {
        TrainingDTO training = trainingService.getTrainingById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Training", "id", id));
        
        return ResponseBuilder.success(training);
    }

    @GetMapping("/team/{teamId}")
    @PreAuthorize("@teamSecurityService.canAccessTeam(#teamId)")
    public ResponseEntity<?> getTrainingsByTeam(@PathVariable Long teamId) {
        List<TrainingDTO> trainings = trainingService.getTrainingsByTeam(teamId);
        return ResponseBuilder.success(trainings);
    }

    @PostMapping
    public ResponseEntity<?> createTraining(@Valid @RequestBody TrainingDTO trainingDTO) {
        Team team = teamService.getTeamById(trainingDTO.getTeamId())
                .orElseThrow(() -> new ResourceNotFoundException("Team", "id", trainingDTO.getTeamId()));
        
        TrainingDTO createdTraining = trainingService.createTraining(trainingDTO);
        return ResponseBuilder.created(createdTraining);
    }

    @PutMapping("/{id}")
    @PreAuthorize("@trainingSecurityService.canEditTraining(#id)")
    public ResponseEntity<?> updateTraining(@PathVariable Long id, @Valid @RequestBody TrainingDTO trainingDTO) {
        // Verifica che l'allenamento esista
        trainingService.getTrainingById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Training", "id", id));
        
        trainingDTO.setId(id); // Assicurati che l'ID sia impostato correttamente
        TrainingDTO updatedTraining = trainingService.updateTraining(trainingDTO);
        return ResponseBuilder.success("Training updated successfully", updatedTraining);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@trainingSecurityService.canEditTraining(#id)")
    public ResponseEntity<?> deleteTraining(@PathVariable Long id) {
        trainingService.deleteTraining(id);
        return ResponseBuilder.success("Training deleted successfully", null);
    }
}