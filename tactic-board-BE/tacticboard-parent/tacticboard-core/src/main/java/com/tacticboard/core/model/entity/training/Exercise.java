package com.tacticboard.core.model.entity.training;

import com.tacticboard.core.model.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "exercises")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Exercise extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(length = 2000)
    private String description;
    
    @Column(name = "duration_minutes")
    private Integer durationMinutes;
    
    @Column(name = "difficulty_level")
    @Enumerated(EnumType.STRING)
    private DifficultyLevel difficultyLevel;
    
    @Column(name = "equipment_needed")
    private String equipmentNeeded;
    
    @Column(name = "diagram_url")
    private String diagramUrl;
    
    @Column(name = "video_url")
    private String videoUrl;
    
    @Column(name = "is_public")
    private boolean isPublic = false;
    
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    
    @ManyToMany(mappedBy = "exercises")
    private Set<TrainingSession> trainingSessions = new HashSet<>();
    
    public enum DifficultyLevel {
        BEGINNER,
        INTERMEDIATE,
        ADVANCED,
        PROFESSIONAL
    }
}