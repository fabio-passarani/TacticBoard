package com.tacticboard.persistence.repository;

import com.tacticboard.core.model.entity.media.Annotation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnnotationRepository extends JpaRepository<Annotation, Long> {
    
    List<Annotation> findByVideoId(Long videoId);
    
    List<Annotation> findByCreatorId(Long creatorId);
    
    List<Annotation> findByVideoIdOrderByTimePointSecondsAsc(Long videoId);
}