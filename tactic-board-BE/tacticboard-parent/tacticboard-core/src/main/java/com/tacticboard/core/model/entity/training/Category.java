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
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Category extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String name;
    
    @Column(length = 500)
    private String description;
    
    @Column(name = "icon_url")
    private String iconUrl;
    
    @Column(name = "color_code")
    private String colorCode;
    
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private Set<Exercise> exercises = new HashSet<>();
}