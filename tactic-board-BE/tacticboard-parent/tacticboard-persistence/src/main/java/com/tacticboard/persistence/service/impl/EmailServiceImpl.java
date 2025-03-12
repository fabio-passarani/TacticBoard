package com.tacticboard.persistence.service.impl;

import com.tacticboard.core.service.EmailService;
import com.tacticboard.core.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.Map;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender emailSender;
    private final TemplateEngine templateEngine;
    private final String fromEmail;
    private final boolean emailEnabled;

    @Autowired
    public EmailServiceImpl(
            JavaMailSender emailSender,
            TemplateEngine templateEngine,
            @Value("${spring.mail.username:noreply@tacticboard.com}") String fromEmail,
            @Value("${app.email.enabled:false}") boolean emailEnabled) {
        this.emailSender = emailSender;
        this.templateEngine = templateEngine;
        this.fromEmail = fromEmail;
        this.emailEnabled = emailEnabled;
    }

    @Override
    public void sendSimpleMessage(String to, String subject, String text) {
        if (!emailEnabled) {
            // Log email instead of sending if disabled
            logEmail(to, subject, text);
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
    }

    @Override
    public void sendHtmlMessage(String to, String subject, String htmlContent) {
        if (!emailEnabled) {
            // Log email instead of sending if disabled
            logEmail(to, subject, htmlContent);
            return;
        }

        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            emailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send HTML email", e);
        }
    }

    @Override
    public void sendTemplateMessage(String to, String subject, String templateName, Map<String, Object> templateModel) {
        if (!emailEnabled) {
            // Log email instead of sending if disabled
            logEmail(to, subject, "Template: " + templateName + ", Model: " + templateModel);
            return;
        }

        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            Context context = new Context();
            context.setVariables(templateModel);
            String htmlContent = templateEngine.process(templateName, context);

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            emailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send template email", e);
        }
    }

    @Override
    public void sendWelcomeEmail(String to, String username) {
        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("username", username);
        templateModel.put("appName", Constants.APP_NAME);
        templateModel.put("loginUrl", "https://tacticboard.com/login");

        sendTemplateMessage(to, Constants.WELCOME_EMAIL_SUBJECT, "welcome-email", templateModel);
    }

    @Override
    public void sendPasswordResetEmail(String to, String resetToken) {
        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("resetToken", resetToken);
        templateModel.put("appName", Constants.APP_NAME);
        templateModel.put("resetUrl", "https://tacticboard.com/reset-password?token=" + resetToken);

        sendTemplateMessage(to, Constants.PASSWORD_RESET_EMAIL_SUBJECT, "password-reset-email", templateModel);
    }

    @Override
    public void sendSubscriptionConfirmationEmail(String to, String username, String planName, String startDate,
            String endDate) {
        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("username", username);
        templateModel.put("planName", planName);
        templateModel.put("startDate", startDate);
        templateModel.put("endDate", endDate);
        templateModel.put("appName", Constants.APP_NAME);

        sendTemplateMessage(to, Constants.SUBSCRIPTION_CONFIRMATION_SUBJECT, "subscription-confirmation-email",
                templateModel);
    }

    private void logEmail(String to, String subject, String content) {
        System.out.println("Email disabled. Would have sent email:");
        System.out.println("To: " + to);
        System.out.println("Subject: " + subject);
        System.out.println("Content: " + content);
    }

    @Override
    public void sendSubscriptionConfirmation(String to, String subscriptionType, String endDate) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'sendSubscriptionConfirmation'");
    }
}