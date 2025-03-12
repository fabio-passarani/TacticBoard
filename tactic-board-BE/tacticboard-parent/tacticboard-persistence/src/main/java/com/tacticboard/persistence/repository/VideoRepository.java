package com.tacticboard.persistence.repository;

import com.tacticboard.core.model.entity.media.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {
    
    List<Video> findByTeamId(Long teamId);
    
    List<Video> findByUploaderId(Long uploaderId);
    
    List<Video> findByIsPublicTrue();
}