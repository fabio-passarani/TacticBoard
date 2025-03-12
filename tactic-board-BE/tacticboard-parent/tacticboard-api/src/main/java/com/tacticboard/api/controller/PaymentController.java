package com.tacticboard.api.controller;

import com.tacticboard.api.util.ResponseBuilder;
import com.tacticboard.core.exception.ResourceNotFoundException;
import com.tacticboard.core.model.dto.PaymentDTO;
import com.tacticboard.core.model.dto.SubscriptionDTO;
import com.tacticboard.core.service.PaymentService;
import com.tacticboard.core.service.UserService;
import com.tacticboard.security.util.SecurityUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final UserService userService;

    @Autowired
    public PaymentController(PaymentService paymentService, UserService userService) {
        this.paymentService = paymentService;
        this.userService = userService;
    }

    @GetMapping("/plans")
    public ResponseEntity<?> getSubscriptionPlans() {
        List<SubscriptionDTO> plans = paymentService.getSubscriptionPlans();
        return ResponseBuilder.success(plans);
    }

    @GetMapping("/user/subscription")
    public ResponseEntity<?> getCurrentUserSubscription() {
        String username = SecurityUtils.getCurrentUsername()
                .orElseThrow(() -> new IllegalStateException("No authenticated user found"));
        
        SubscriptionDTO subscription = paymentService.getUserSubscription(username);
        return ResponseBuilder.success(subscription);
    }

    @GetMapping("/user/{userId}/subscription")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isCurrentUser(#userId)")
    public ResponseEntity<?> getUserSubscription(@PathVariable Long userId) {
        SubscriptionDTO subscription = paymentService.getUserSubscriptionById(userId);
        return ResponseBuilder.success(subscription);
    }

    @PostMapping("/subscribe")
    public ResponseEntity<?> subscribe(@Valid @RequestBody PaymentDTO paymentDTO) {
        String username = SecurityUtils.getCurrentUsername()
                .orElseThrow(() -> new IllegalStateException("No authenticated user found"));
        
        SubscriptionDTO subscription = paymentService.processSubscription(username, paymentDTO);
        return ResponseBuilder.success("Subscription processed successfully", subscription);
    }

    @PostMapping("/cancel")
    public ResponseEntity<?> cancelSubscription() {
        String username = SecurityUtils.getCurrentUsername()
                .orElseThrow(() -> new IllegalStateException("No authenticated user found"));
        
        paymentService.cancelSubscription(username);
        return ResponseBuilder.success("Subscription cancelled successfully", null);
    }

    @GetMapping("/history")
    public ResponseEntity<?> getPaymentHistory() {
        String username = SecurityUtils.getCurrentUsername()
                .orElseThrow(() -> new IllegalStateException("No authenticated user found"));
        
        List<PaymentDTO> paymentHistory = paymentService.getPaymentHistory(username);
        return ResponseBuilder.success(paymentHistory);
    }

    @GetMapping("/invoice/{invoiceId}")
    public ResponseEntity<?> getInvoice(@PathVariable String invoiceId) {
        String username = SecurityUtils.getCurrentUsername()
                .orElseThrow(() -> new IllegalStateException("No authenticated user found"));
        
        Object invoice = paymentService.getInvoice(username, invoiceId);
        return ResponseBuilder.success(invoice);
    }

    @PostMapping("/webhook")
    public ResponseEntity<?> handlePaymentWebhook(@RequestBody String payload) {
        paymentService.processWebhook(payload);
        return ResponseBuilder.success("Webhook processed successfully", null);
    }

    @GetMapping("/admin/subscriptions")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllSubscriptions() {
        List<SubscriptionDTO> subscriptions = paymentService.getAllSubscriptions();
        return ResponseBuilder.success(subscriptions);
    }
}