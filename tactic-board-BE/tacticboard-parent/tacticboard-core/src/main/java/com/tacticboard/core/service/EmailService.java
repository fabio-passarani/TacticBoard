package com.tacticboard.core.service;

public interface EmailService {
    
    void sendSimpleMessage(String to, String subject, String text);
    
    void sendHtmlMessage(String to, String subject, String htmlContent);
    
    void sendPasswordResetEmail(String to, String resetToken);
    
    void sendWelcomeEmail(String to, String username);
    
    void sendSubscriptionConfirmation(String to, String subscriptionType, String endDate);
}