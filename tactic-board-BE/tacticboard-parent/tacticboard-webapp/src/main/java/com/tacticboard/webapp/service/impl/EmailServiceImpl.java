package com.tacticboard.webapp.service.impl;

import com.tacticboard.core.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.Map;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    
    @Value("${spring.mail.username:noreply@tacticboard.com}")
    private String fromEmail;
    
    @Value("${app.email.enabled:false}")
    private boolean emailEnabled;

    @Autowired
    public EmailServiceImpl(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    @Async
    @Override
    public void sendEmail(String to, String subject, String templateName, Map<String, Object> templateModel) {
        if (!emailEnabled) {
            log.info("Email sending is disabled. Would have sent email to {} with subject: {}", to, subject);
            return;
        }
        
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            
            Context context = new Context();
            if (templateModel != null) {
                templateModel.forEach(context::setVariable);
            }
            
            String htmlContent = templateEngine.process(templateName, context);
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
            log.info("Email sent to {} with subject: {}", to, subject);
        } catch (MessagingException e) {
            log.error("Failed to send email to {} with subject: {}", to, subject, e);
        }
    }

    @Override
    public void sendWelcomeEmail(String to, String name) {
        Map<String, Object> templateModel = Map.of(
                "name", name,
                "appName", "TacticBoard"
        );
        
        sendEmail(to, "Welcome to TacticBoard", "welcome", templateModel);
    }

    @Override
    public void sendPasswordResetEmail(String to, String resetLink) {
        Map<String, Object> templateModel = Map.of(
                "resetLink", resetLink,
                "expiryHours", 24
        );
        
        sendEmail(to, "Reset Your TacticBoard Password", "password-reset", templateModel);
    }

    @Override
    public void sendSubscriptionConfirmation(String to, String name, String planName) {
        Map<String, Object> templateModel = Map.of(
                "name", name,
                "planName", planName,
                "supportEmail", "support@tacticboard.com"
        );
        
        sendEmail(to, "TacticBoard Subscription Confirmation", "subscription-confirmation", templateModel);
    }
}