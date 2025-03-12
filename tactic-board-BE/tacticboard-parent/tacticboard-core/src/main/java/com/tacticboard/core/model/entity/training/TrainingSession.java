package com.tacticboard.core.model.entity.training;

import com.tacticboard.core.model.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "training_sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TrainingSession extends BaseEntity {

    @Column(nullable = false)
    private String title;
    
    @Column(length = 1000)
    private String description;
    
    @Column(name = "session_date")
    private LocalDateTime sessionDate;
    
    @Column(name = "duration_minutes")
    private Integer durationMinutes;
    
    @Column(name = "location")
    private String location;
    
    @Column(name = "notes", length = 2000)
    private String notes;
    
    @Enumerated(EnumType.STRING)
    private SessionStatus status = SessionStatus.PLANNED;
    
    @ManyToOne
    @JoinColumn(name = "training_plan_id", nullable = false)
    private TrainingPlan trainingPlan;
    
    @ManyToMany
    @JoinTable(
        name = "session_exercises",
        joinColumns = @JoinColumn(name = "session_id"),
        inverseJoinColumns = @JoinColumn(name = "exercise_id")
    )
    private Set<Exercise> exercises = new HashSet<>();
    
    @OneToMany(mappedBy = "trainingSession", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Attendance> attendances = new HashSet<>();
    
    public enum SessionStatus {
        PLANNED,
        IN_PROGRESS,
        COMPLETED,
        CANCELLED
    }
}