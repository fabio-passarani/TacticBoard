package com.tacticboard.core.model.entity.training;

import com.tacticboard.core.model.entity.BaseEntity;
import com.tacticboard.core.model.entity.team.Team;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "training_plans")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TrainingPlan extends BaseEntity {

    @Column(nullable = false)
    private String name;
    
    @Column(length = 1000)
    private String description;
    
    @Column(name = "start_date")
    private LocalDate startDate;
    
    @Column(name = "end_date")
    private LocalDate endDate;
    
    @Column(name = "is_active")
    private boolean isActive = true;
    
    @ManyToOne
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;
    
    @OneToMany(mappedBy = "trainingPlan", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TrainingSession> sessions = new HashSet<>();
}