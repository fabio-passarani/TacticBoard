package com.tacticboard.api.controller;

import com.tacticboard.api.util.ResponseBuilder;
import com.tacticboard.core.exception.ResourceNotFoundException;
import com.tacticboard.core.model.dto.ExerciseDTO;
import com.tacticboard.core.model.entity.team.Team;
import com.tacticboard.core.service.ExerciseService;
import com.tacticboard.core.service.TeamService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exercises")
public class ExerciseController {

    private final ExerciseService exerciseService;
    private final TeamService teamService;

    @Autowired
    public ExerciseController(ExerciseService exerciseService, TeamService teamService) {
        this.exerciseService = exerciseService;
        this.teamService = teamService;
    }

    @GetMapping
    public ResponseEntity<?> getAllExercises() {
        List<ExerciseDTO> exercises = exerciseService.getAllExercises();
        return ResponseBuilder.success(exercises);
    }

    @GetMapping("/{id}")
    @PreAuthorize("@exerciseSecurityService.canAccessExercise(#id)")
    public ResponseEntity<?> getExerciseById(@PathVariable Long id) {
        ExerciseDTO exercise = exerciseService.getExerciseById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exercise", "id", id));
        
        return ResponseBuilder.success(exercise);
    }

    @GetMapping("/team/{teamId}")
    @PreAuthorize("@teamSecurityService.canAccessTeam(#teamId)")
    public ResponseEntity<?> getExercisesByTeam(@PathVariable Long teamId) {
        List<ExerciseDTO> exercises = exerciseService.getExercisesByTeam(teamId);
        return ResponseBuilder.success(exercises);
    }

    @PostMapping
    public ResponseEntity<?> createExercise(@Valid @RequestBody ExerciseDTO exerciseDTO) {
        Team team = teamService.getTeamById(exerciseDTO.getTeamId())
                .orElseThrow(() -> new ResourceNotFoundException("Team", "id", exerciseDTO.getTeamId()));
        
        ExerciseDTO createdExercise = exerciseService.createExercise(exerciseDTO);
        return ResponseBuilder.created(createdExercise);
    }

    @PutMapping("/{id}")
    @PreAuthorize("@exerciseSecurityService.canEditExercise(#id)")
    public ResponseEntity<?> updateExercise(@PathVariable Long id, @Valid @RequestBody ExerciseDTO exerciseDTO) {
        // Verifica che l'esercizio esista
        exerciseService.getExerciseById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exercise", "id", id));
        
        exerciseDTO.setId(id); // Assicurati che l'ID sia impostato correttamente
        ExerciseDTO updatedExercise = exerciseService.updateExercise(exerciseDTO);
        return ResponseBuilder.success("Exercise updated successfully", updatedExercise);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@exerciseSecurityService.canEditExercise(#id)")
    public ResponseEntity<?> deleteExercise(@PathVariable Long id) {
        exerciseService.deleteExercise(id);
        return ResponseBuilder.success("Exercise deleted successfully", null);
    }
}