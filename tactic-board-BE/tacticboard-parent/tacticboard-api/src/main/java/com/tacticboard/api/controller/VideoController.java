package com.tacticboard.api.controller;

import com.tacticboard.api.util.ResponseBuilder;
import com.tacticboard.core.exception.ResourceNotFoundException;
import com.tacticboard.core.model.dto.VideoDTO;
import com.tacticboard.core.model.entity.team.Team;
import com.tacticboard.core.service.TeamService;
import com.tacticboard.core.service.VideoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/videos")
public class VideoController {

    private final VideoService videoService;
    private final TeamService teamService;

    @Autowired
    public VideoController(VideoService videoService, TeamService teamService) {
        this.videoService = videoService;
        this.teamService = teamService;
    }

    @GetMapping
    public ResponseEntity<?> getAllVideos() {
        List<VideoDTO> videos = videoService.getAllVideos();
        return ResponseBuilder.success(videos);
    }

    @GetMapping("/{id}")
    @PreAuthorize("@videoSecurityService.canAccessVideo(#id)")
    public ResponseEntity<?> getVideoById(@PathVariable Long id) {
        VideoDTO video = videoService.getVideoById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Video", "id", id));
        
        return ResponseBuilder.success(video);
    }

    @GetMapping("/team/{teamId}")
    @PreAuthorize("@teamSecurityService.canAccessTeam(#teamId)")
    public ResponseEntity<?> getVideosByTeam(@PathVariable Long teamId) {
        List<VideoDTO> videos = videoService.getVideosByTeam(teamId);
        return ResponseBuilder.success(videos);
    }

    @PostMapping
    public ResponseEntity<?> createVideo(@Valid @RequestBody VideoDTO videoDTO) {
        Team team = teamService.getTeamById(videoDTO.getTeamId())
                .orElseThrow(() -> new ResourceNotFoundException("Team", "id", videoDTO.getTeamId()));
        
        VideoDTO createdVideo = videoService.createVideo(videoDTO);
        return ResponseBuilder.created(createdVideo);
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadVideo(@RequestParam("file") MultipartFile file, 
                                         @RequestParam("teamId") Long teamId,
                                         @RequestParam("title") String title,
                                         @RequestParam("description") String description) {
        Team team = teamService.getTeamById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team", "id", teamId));
        
        VideoDTO videoDTO = videoService.uploadVideo(file, teamId, title, description);
        return ResponseBuilder.created(videoDTO);
    }

    @PutMapping("/{id}")
    @PreAuthorize("@videoSecurityService.canEditVideo(#id)")
    public ResponseEntity<?> updateVideo(@PathVariable Long id, @Valid @RequestBody VideoDTO videoDTO) {
        // Verifica che il video esista
        videoService.getVideoById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Video", "id", id));
        
        videoDTO.setId(id); // Assicurati che l'ID sia impostato correttamente
        VideoDTO updatedVideo = videoService.updateVideo(videoDTO);
        return ResponseBuilder.success("Video updated successfully", updatedVideo);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@videoSecurityService.canEditVideo(#id)")
    public ResponseEntity<?> deleteVideo(@PathVariable Long id) {
        videoService.deleteVideo(id);
        return ResponseBuilder.success("Video deleted successfully", null);
    }
}