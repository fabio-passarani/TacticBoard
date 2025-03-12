package com.tacticboard.core.model.entity.media;

import com.tacticboard.core.model.entity.BaseEntity;
import com.tacticboard.core.model.entity.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "annotations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Annotation extends BaseEntity {

    @Column(name = "time_point_seconds", nullable = false)
    private Integer timePointSeconds;
    
    @Column(length = 1000)
    private String comment;
    
    @Column(name = "drawing_data", columnDefinition = "TEXT")
    private String drawingData;
    
    @ManyToOne
    @JoinColumn(name = "video_id", nullable = false)
    private Video video;
    
    @ManyToOne
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;
}