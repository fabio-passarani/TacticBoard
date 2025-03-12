package com.tacticboard.webapp.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Map;

/**
 * Service for sending emails using templates.
 */
@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final boolean emailEnabled;

    /**
     * Initializes the email service with required dependencies.
     * 
     * @param mailSender The JavaMailSender for sending emails
     * @param templateEngine The Thymeleaf template engine for processing templates
     * @param emailEnabled Flag to enable/disable email sending
     */
    @Autowired
    public EmailService(JavaMailSender mailSender, 
                        TemplateEngine templateEngine,
                        @Value("${app.email.enabled:false}") boolean emailEnabled) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.emailEnabled = emailEnabled;
    }

    /**
     * Sends an email using a Thymeleaf template.
     * 
     * @param to The recipient email address
     * @param subject The email subject
     * @param templateName The name of the template to use
     * @param variables The variables to be used in the template
     * @return true if the email was sent successfully, false otherwise
     */
    public boolean sendTemplateEmail(String to, String subject, String templateName, Map<String, Object> variables) {
        if (!emailEnabled) {
            logger.info("Email sending is disabled. Would have sent email to: {}", to);
            return true;
        }

        try {
            // Prepare the context with variables
            Context context = new Context();
            variables.forEach(context::setVariable);

            // Process the template
            String content = templateEngine.process("email/" + templateName, context);

            // Create the email message
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);

            // Send the email
            mailSender.send(message);
            logger.info("Email sent successfully to: {}", to);
            return true;
        } catch (MessagingException e) {
            logger.error("Failed to send email to: {}", to, e);
            return false;
        }
    }

    /**
     * Sends a welcome email to a new user.
     * 
     * @param to The recipient email address
     * @param name The name of the recipient
     * @return true if the email was sent successfully, false otherwise
     */
    public boolean sendWelcomeEmail(String to, String name) {
        Map<String, Object> variables = Map.of(
            "name", name,
            "appName", "TacticBoard"
        );
        return sendTemplateEmail(to, "Welcome to TacticBoard", "welcome.html", variables);
    }

    /**
     * Sends a password reset email.
     * 
     * @param to The recipient email address
     * @param resetLink The password reset link
     * @param expiryHours The number of hours until the reset link expires
     * @return true if the email was sent successfully, false otherwise
     */
    public boolean sendPasswordResetEmail(String to, String resetLink, int expiryHours) {
        Map<String, Object> variables = Map.of(
            "resetLink", resetLink,
            "expiryHours", expiryHours
        );
        return sendTemplateEmail(to, "Reset Your TacticBoard Password", "password-reset.html", variables);
    }

    /**
     * Sends a subscription confirmation email.
     * 
     * @param to The recipient email address
     * @param name The name of the recipient
     * @param planName The name of the subscription plan
     * @param supportEmail The support email address
     * @return true if the email was sent successfully, false otherwise
     */
    public boolean sendSubscriptionConfirmationEmail(String to, String name, String planName, String supportEmail) {
        Map<String, Object> variables = Map.of(
            "name", name,
            "planName", planName,
            "supportEmail", supportEmail
        );
        return sendTemplateEmail(to, "Subscription Confirmation", "subscription-confirmation.html", variables);
    }

    /**
     * Sends a training reminder email.
     * 
     * @param to The recipient email address
     * @param coachName The name of the coach
     * @param teamName The name of the team
     * @param trainingTitle The title of the training
     * @param trainingDate The date of the training
     * @param trainingTime The time of the training
     * @param trainingDuration The duration of the training in minutes
     * @return true if the email was sent successfully, false otherwise
     */
    public boolean sendTrainingReminderEmail(String to, String coachName, String teamName, 
                                            String trainingTitle, String trainingDate, 
                                            String trainingTime, String trainingDuration) {
        Map<String, Object> variables = Map.of(
            "coachName", coachName,
            "teamName", teamName,
            "trainingTitle", trainingTitle,
            "trainingDate", trainingDate,
            "trainingTime", trainingTime,
            "trainingDuration", trainingDuration
        );
        return sendTemplateEmail(to, "Training Reminder: " + trainingTitle, "training-reminder.html", variables);
    }
}