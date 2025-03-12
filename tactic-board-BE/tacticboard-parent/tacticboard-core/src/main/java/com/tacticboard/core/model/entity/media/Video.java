package com.tacticboard.core.model.entity.media;

import com.tacticboard.core.model.entity.BaseEntity;
import com.tacticboard.core.model.entity.team.Team;
import com.tacticboard.core.model.entity.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "videos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Video extends BaseEntity {

    @Column(nullable = false)
    private String title;
    
    @Column(length = 1000)
    private String description;
    
    @Column(name = "video_url", nullable = false)
    private String videoUrl;
    
    @Column(name = "thumbnail_url")
    private String thumbnailUrl;
    
    @Column(name = "duration_seconds")
    private Integer durationSeconds;
    
    @Column(name = "file_size_mb")
    private Double fileSizeMb;
    
    @Column(name = "is_public")
    private boolean isPublic = false;
    
    @Enumerated(EnumType.STRING)
    private VideoType videoType;
    
    @ManyToOne
    @JoinColumn(name = "uploader_id", nullable = false)
    private User uploader;
    
    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;
    
    @OneToMany(mappedBy = "video", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Annotation> annotations = new HashSet<>();
    
    public enum VideoType {
        MATCH,
        TRAINING,
        ANALYSIS,
        TUTORIAL,
        OTHER
    }
}