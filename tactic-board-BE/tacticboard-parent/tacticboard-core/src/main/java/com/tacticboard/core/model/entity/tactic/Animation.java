package com.tacticboard.core.model.entity.tactic;

import com.tacticboard.core.model.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "animations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Animation extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(name = "sequence_order")
    private Integer sequenceOrder;

    @Column(name = "duration_seconds")
    private Integer durationSeconds;

    @Column(name = "animation_data", columnDefinition = "TEXT")
    private String animationData;

    @ManyToOne
    @JoinColumn(name = "tactic_id", nullable = false)
    private Tactic tactic;
}