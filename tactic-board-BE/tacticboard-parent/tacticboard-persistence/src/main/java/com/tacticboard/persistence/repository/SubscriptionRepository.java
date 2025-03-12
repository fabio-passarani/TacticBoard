package com.tacticboard.persistence.repository;

import com.tacticboard.core.model.entity.payment.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    
    List<Subscription> findByUserId(Long userId);
    
    Optional<Subscription> findByUserIdAndIsActiveTrue(Long userId);
    
    List<Subscription> findByEndDateBefore(LocalDate date);
    
    List<Subscription> findByIsActiveTrueAndEndDateBefore(LocalDate date);
}