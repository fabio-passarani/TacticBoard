package com.tacticboard.persistence.service.impl;

import com.tacticboard.core.exception.ResourceNotFoundException;
import com.tacticboard.core.model.entity.video.Video;
import com.tacticboard.core.model.entity.video.VideoAnnotation;
import com.tacticboard.core.service.VideoService;
import com.tacticboard.persistence.repository.TeamRepository;
import com.tacticboard.persistence.repository.VideoAnnotationRepository;
import com.tacticboard.persistence.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class VideoServiceImpl implements VideoService {

    private final VideoRepository videoRepository;
    private final VideoAnnotationRepository videoAnnotationRepository;
    private final TeamRepository teamRepository;

    @Autowired
    public VideoServiceImpl(
            VideoRepository videoRepository,
            VideoAnnotationRepository videoAnnotationRepository,
            TeamRepository teamRepository) {
        this.videoRepository = videoRepository;
        this.videoAnnotationRepository = videoAnnotationRepository;
        this.teamRepository = teamRepository;
    }

    @Override
    public Video createVideo(Video video) {
        if (!teamRepository.existsById(video.getTeam().getId())) {
            throw new ResourceNotFoundException("Team", "id", video.getTeam().getId());
        }
        
        video.setUploadedAt(LocalDateTime.now());
        return videoRepository.save(video);
    }

    @Override
    public Video updateVideo(Video video) {
        if (!videoRepository.existsById(video.getId())) {
            throw new ResourceNotFoundException("Video", "id", video.getId());
        }
        
        return videoRepository.save(video);
    }

    @Override
    public Optional<Video> getVideoById(Long id) {
        return videoRepository.findById(id);
    }

    @Override
    public List<Video> getVideosByTeamId(Long teamId) {
        if (!teamRepository.existsById(teamId)) {
            throw new ResourceNotFoundException("Team", "id", teamId);
        }
        
        return videoRepository.findByTeamId(teamId);
    }

    @Override
    public void deleteVideo(Long id) {
        if (!videoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Video", "id", id);
        }
        
        // Delete all annotations first
        videoAnnotationRepository.deleteByVideoId(id);
        videoRepository.deleteById(id);
    }

    @Override
    public VideoAnnotation createVideoAnnotation(VideoAnnotation annotation, Long videoId) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new ResourceNotFoundException("Video", "id", videoId));
        
        annotation.setVideo(video);
        annotation.setCreatedAt(LocalDateTime.now());
        
        return videoAnnotationRepository.save(annotation);
    }

    @Override
    public VideoAnnotation updateVideoAnnotation(VideoAnnotation annotation) {
        if (!videoAnnotationRepository.existsById(annotation.getId())) {
            throw new ResourceNotFoundException("VideoAnnotation", "id", annotation.getId());
        }
        
        return videoAnnotationRepository.save(annotation);
    }

    @Override
    public Optional<VideoAnnotation> getVideoAnnotationById(Long id) {
        return videoAnnotationRepository.findById(id);
    }

    @Override
    public List<VideoAnnotation> getVideoAnnotationsByVideoId(Long videoId) {
        if (!videoRepository.existsById(videoId)) {
            throw new ResourceNotFoundException("Video", "id", videoId);
        }
        
        return videoAnnotationRepository.findByVideoId(videoId);
    }

    @Override
    public void deleteVideoAnnotation(Long id) {
        if (!videoAnnotationRepository.existsById(id)) {
            throw new ResourceNotFoundException("VideoAnnotation", "id", id);
        }
        
        videoAnnotationRepository.deleteById(id);
    }

    @Override
    public List<Video> searchVideos(String query, Long teamId) {
        if (teamId != null) {
            if (!teamRepository.existsById(teamId)) {
                throw new ResourceNotFoundException("Team", "id", teamId);
            }
            return videoRepository.findByTitleContainingAndTeamId(query, teamId);
        } else {
            return videoRepository.findByTitleContaining(query);
        }
    }
}