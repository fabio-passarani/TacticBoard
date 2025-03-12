package com.tacticboard.api.controller;

import com.tacticboard.api.util.ResponseBuilder;
import com.tacticboard.core.model.dto.TacticDTO;
import com.tacticboard.core.exception.ResourceNotFoundException;
import com.tacticboard.core.model.entity.tactic.Tactic;
import com.tacticboard.core.model.entity.team.Team;
import com.tacticboard.core.service.TacticService;
import com.tacticboard.core.service.TeamService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tactics")
public class TacticController {

    private final TacticService tacticService;
    private final TeamService teamService;

    @Autowired
    public TacticController(TacticService tacticService, TeamService teamService) {
        this.tacticService = tacticService;
        this.teamService = teamService;
    }

    @GetMapping
    public ResponseEntity<?> getAllTactics() {
        List<TacticDTO> tactics = tacticService.getAllTactics();
        return ResponseBuilder.success(tactics);
    }

    @GetMapping("/{id}")
    @PreAuthorize("@tacticSecurityService.canAccessTactic(#id)")
    public ResponseEntity<?> getTacticById(@PathVariable Long id) {
        TacticDTO tactic = tacticService.getTacticById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tactic", "id", id));

        return ResponseBuilder.success(tactic);
    }

    @GetMapping("/team/{teamId}")
    @PreAuthorize("@teamSecurityService.canAccessTeam(#teamId)")
    public ResponseEntity<?> getTacticsByTeam(@PathVariable Long teamId) {
        List<TacticDTO> tactics = tacticService.getTacticsByTeam(teamId);
        return ResponseBuilder.success(tactics);
    }

    @PostMapping
    public ResponseEntity<?> createTactic(@Valid @RequestBody TacticDTO tacticDTO) {
        Team team = teamService.getTeamById(tacticDTO.getTeamId())
                .orElseThrow(() -> new ResourceNotFoundException("Team", "id", tacticDTO.getTeamId()));

        TacticDTO createdTactic = tacticService.createTactic(tacticDTO);
        return ResponseBuilder.created(createdTactic);
    }

    @PutMapping("/{id}")
    @PreAuthorize("@tacticSecurityService.canEditTactic(#id)")
    public ResponseEntity<?> updateTactic(@PathVariable Long id, @Valid @RequestBody TacticDTO tacticDTO) {
        // Verifica che la tattica esista
        tacticService.getTacticById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tactic", "id", id));

        tacticDTO.setId(id); // Assicurati che l'ID sia impostato correttamente
        TacticDTO updatedTactic = tacticService.updateTactic(tacticDTO);
        return ResponseBuilder.success("Tactic updated successfully", updatedTactic);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@tacticSecurityService.canEditTactic(#id)")
    public ResponseEntity<?> deleteTactic(@PathVariable Long id) {
        tacticService.deleteTactic(id);
        return ResponseBuilder.success("Tactic deleted successfully", null);
    }
}