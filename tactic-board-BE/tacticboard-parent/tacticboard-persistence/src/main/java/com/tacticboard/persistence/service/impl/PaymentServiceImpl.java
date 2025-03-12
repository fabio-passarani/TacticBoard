package com.tacticboard.persistence.service.impl;

import com.tacticboard.core.exception.BusinessLogicException;
import com.tacticboard.core.exception.ResourceNotFoundException;
import com.tacticboard.core.model.entity.payment.Payment;
import com.tacticboard.core.model.entity.payment.Subscription;
import com.tacticboard.core.service.PaymentService;
import com.tacticboard.persistence.repository.PaymentRepository;
import com.tacticboard.persistence.repository.SubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final SubscriptionRepository subscriptionRepository;
    private final PaymentRepository paymentRepository;

    @Autowired
    public PaymentServiceImpl(SubscriptionRepository subscriptionRepository, PaymentRepository paymentRepository) {
        this.subscriptionRepository = subscriptionRepository;
        this.paymentRepository = paymentRepository;
    }

    @Override
    public Subscription createSubscription(Subscription subscription) {
        // Check if user already has an active subscription
        Optional<Subscription> existingSubscription = subscriptionRepository.findByUserIdAndIsActiveTrue(subscription.getUser().getId());
        if (existingSubscription.isPresent()) {
            throw new BusinessLogicException("User already has an active subscription");
        }
        return subscriptionRepository.save(subscription);
    }

    @Override
    public Subscription updateSubscription(Subscription subscription) {
        if (!subscriptionRepository.existsById(subscription.getId())) {
            throw new ResourceNotFoundException("Subscription", "id", subscription.getId());
        }
        return subscriptionRepository.save(subscription);
    }

    @Override
    public Optional<Subscription> getSubscriptionById(Long id) {
        return subscriptionRepository.findById(id);
    }

    @Override
    public List<Subscription> getSubscriptionsByUserId(Long userId) {
        return subscriptionRepository.findByUserId(userId);
    }

    @Override
    public Optional<Subscription> getActiveSubscriptionByUserId(Long userId) {
        return subscriptionRepository.findByUserIdAndIsActiveTrue(userId);
    }

    @Override
    public void cancelSubscription(Long id) {
        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription", "id", id));
        
        subscription.setActive(false);
        subscription.setAutoRenew(false);
        subscriptionRepository.save(subscription);
    }

    @Override
    public Payment processPayment(Payment payment) {
        // Validate subscription exists
        if (!subscriptionRepository.existsById(payment.getSubscription().getId())) {
            throw new ResourceNotFoundException("Subscription", "id", payment.getSubscription().getId());
        }
        
        // Set payment status to PENDING initially
        payment.setStatus(Payment.PaymentStatus.PENDING);
        
        // Save payment
        Payment savedPayment = paymentRepository.save(payment);
        
        // In a real implementation, we would integrate with a payment gateway here
        // For now, we'll just simulate a successful payment
        savedPayment.setStatus(Payment.PaymentStatus.COMPLETED);
        return paymentRepository.save(savedPayment);
    }

    @Override
    public Optional<Payment> getPaymentById(Long id) {
        return paymentRepository.findById(id);
    }

    @Override
    public List<Payment> getPaymentsBySubscriptionId(Long subscriptionId) {
        if (!subscriptionRepository.existsById(subscriptionId)) {
            throw new ResourceNotFoundException("Subscription", "id", subscriptionId);
        }
        return paymentRepository.findBySubscriptionId(subscriptionId);
    }

    @Override
    public boolean verifyPayment(String transactionId) {
        // In a real implementation, this would verify with the payment gateway
        // For now, we'll just check if the payment exists and is completed
        Optional<Payment> payment = paymentRepository.findByTransactionId(transactionId);
        return payment.isPresent() && payment.get().getStatus() == Payment.PaymentStatus.COMPLETED;
    }
}