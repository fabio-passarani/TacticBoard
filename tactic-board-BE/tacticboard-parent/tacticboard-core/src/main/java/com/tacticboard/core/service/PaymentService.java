package com.tacticboard.core.service;

import com.tacticboard.core.model.entity.payment.Payment;
import com.tacticboard.core.model.entity.payment.Subscription;

import java.util.List;
import java.util.Optional;

public interface PaymentService {
    
    Subscription createSubscription(Subscription subscription);
    
    Subscription updateSubscription(Subscription subscription);
    
    Optional<Subscription> getSubscriptionById(Long id);
    
    List<Subscription> getSubscriptionsByUserId(Long userId);
    
    Optional<Subscription> getActiveSubscriptionByUserId(Long userId);
    
    void cancelSubscription(Long id);
    
    Payment processPayment(Payment payment);
    
    Optional<Payment> getPaymentById(Long id);
    
    List<Payment> getPaymentsBySubscriptionId(Long subscriptionId);
    
    boolean verifyPayment(String transactionId);
}