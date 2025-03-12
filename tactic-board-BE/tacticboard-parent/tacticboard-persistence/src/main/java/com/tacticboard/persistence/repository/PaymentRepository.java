package com.tacticboard.persistence.repository;

import com.tacticboard.core.model.entity.payment.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    List<Payment> findBySubscriptionId(Long subscriptionId);
    
    Optional<Payment> findByTransactionId(String transactionId);
    
    List<Payment> findByPaymentDateBetween(LocalDateTime start, LocalDateTime end);
}