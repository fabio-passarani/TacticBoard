package com.tacticboard.core.model.entity.training;

import com.tacticboard.core.model.entity.BaseEntity;
import com.tacticboard.core.model.entity.team.Player;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "attendances")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Attendance extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;
    
    @ManyToOne
    @JoinColumn(name = "training_session_id", nullable = false)
    private TrainingSession trainingSession;
    
    @Enumerated(EnumType.STRING)
    private AttendanceStatus status = AttendanceStatus.PRESENT;
    
    @Column(length = 500)
    private String notes;
    
    @Column(name = "performance_rating")
    private Integer performanceRating;
    
    public enum AttendanceStatus {
        PRESENT,
        ABSENT,
        EXCUSED,
        LATE,
        INJURED
    }
}