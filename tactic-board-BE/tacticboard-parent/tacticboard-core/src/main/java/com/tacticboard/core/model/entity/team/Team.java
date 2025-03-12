package com.tacticboard.core.model.entity.team;

import com.tacticboard.core.model.entity.BaseEntity;
import com.tacticboard.core.model.entity.tactic.Tactic;
import com.tacticboard.core.model.entity.training.TrainingPlan;
import com.tacticboard.core.model.entity.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "teams")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Team extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(name = "logo_url")
    private String logoUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    private TeamCategory category;

    @Column(name = "age_group")
    private String ageGroup;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Player> players = new HashSet<>();

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL)
    private Set<Tactic> tactics = new HashSet<>();

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL)
    private Set<TrainingPlan> trainingPlans = new HashSet<>();

    public enum TeamCategory {
        PROFESSIONAL,
        AMATEUR,
        YOUTH,
        SCHOOL
    }
}