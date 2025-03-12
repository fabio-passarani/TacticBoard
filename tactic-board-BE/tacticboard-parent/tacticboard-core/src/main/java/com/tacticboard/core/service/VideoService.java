package com.tacticboard.core.service;

import com.tacticboard.core.model.entity.media.Annotation;
import com.tacticboard.core.model.entity.media.Video;

import java.util.List;
import java.util.Optional;

public interface VideoService {
    
    Video uploadVideo(Video video);
    
    Video updateVideoMetadata(Video video);
    
    Optional<Video> getVideoById(Long id);
    
    List<Video> getVideosByTeamId(Long teamId);
    
    List<Video> getVideosByUserId(Long userId);
    
    void deleteVideo(Long id);
    
    Annotation createAnnotation(Annotation annotation);
    
    Annotation updateAnnotation(Annotation annotation);
    
    Optional<Annotation> getAnnotationById(Long id);
    
    List<Annotation> getAnnotationsByVideoId(Long videoId);
    
    void deleteAnnotation(Long id);
}