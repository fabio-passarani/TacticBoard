package com.tacticboard.core.model.entity.tactic;

import com.tacticboard.core.model.entity.BaseEntity;
import com.tacticboard.core.model.entity.team.Team;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tactics")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Tactic extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(name = "formation")
    private String formation;

    @Column(name = "field_type")
    @Enumerated(EnumType.STRING)
    private FieldType fieldType;

    @Column(name = "is_public")
    private boolean isPublic = false;

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    @Column(name = "layout_data", columnDefinition = "TEXT")
    private String layoutData;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    @OneToMany(mappedBy = "tactic", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Animation> animations = new HashSet<>();

    public enum FieldType {
        FOOTBALL,
        FUTSAL,
        BASKETBALL,
        VOLLEYBALL,
        HANDBALL
    }
}