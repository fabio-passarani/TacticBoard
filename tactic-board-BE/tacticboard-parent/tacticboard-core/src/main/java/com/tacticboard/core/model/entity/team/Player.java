package com.tacticboard.core.model.entity.team;

import com.tacticboard.core.model.entity.BaseEntity;
import com.tacticboard.core.model.entity.training.Attendance;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "players")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Player extends BaseEntity {

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(name = "jersey_number")
    private Integer jerseyNumber;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "height_cm")
    private Integer heightCm;

    @Column(name = "weight_kg")
    private Integer weightKg;

    @Column(name = "photo_url")
    private String photoUrl;

    @Enumerated(EnumType.STRING)
    private Position position;

    @Column(length = 1000)
    private String notes;

    @ManyToOne
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL)
    private Set<Attendance> attendances = new HashSet<>();

    public enum Position {
        GOALKEEPER,
        DEFENDER,
        MIDFIELDER,
        FORWARD
    }
}