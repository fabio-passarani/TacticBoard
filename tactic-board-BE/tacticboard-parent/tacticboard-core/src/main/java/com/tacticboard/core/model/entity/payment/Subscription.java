package com.tacticboard.core.model.entity.payment;

import com.tacticboard.core.model.entity.BaseEntity;
import com.tacticboard.core.model.entity.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "subscriptions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Subscription extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubscriptionType type;
    
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;
    
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;
    
    @Column(name = "is_active")
    private boolean isActive = true;
    
    @Column(name = "auto_renew")
    private boolean autoRenew = false;
    
    @Column(name = "max_teams")
    private Integer maxTeams;
    
    @Column(name = "max_storage_gb")
    private Integer maxStorageGb;
    
    @OneToMany(mappedBy = "subscription", cascade = CascadeType.ALL)
    private Set<Payment> payments = new HashSet<>();
    
    public enum SubscriptionType {
        FREE,
        BASIC,
        PREMIUM,
        PROFESSIONAL,
        ENTERPRISE
    }
}