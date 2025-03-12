package com.tacticboard.webapp.scheduled;

import com.tacticboard.core.model.entity.team.Team;
import com.tacticboard.core.model.entity.training.TrainingPlan;
import com.tacticboard.core.model.entity.user.User;
import com.tacticboard.core.service.EmailService;
import com.tacticboard.persistence.repository.TeamRepository;
import com.tacticboard.persistence.repository.TrainingPlanRepository;
import com.tacticboard.persistence.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class NotificationJob {

    private final TrainingPlanRepository trainingPlanRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    @Autowired
    public NotificationJob(
            TrainingPlanRepository trainingPlanRepository,
            TeamRepository teamRepository,
            UserRepository userRepository,
            EmailService emailService) {
        this.trainingPlanRepository = trainingPlanRepository;
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    @Scheduled(cron = "0 0 8 * * ?") // Every day at 8:00 AM
    public void sendTrainingReminders() {
        log.info("Starting training reminder notifications job");
        
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        
        // Find all training plans scheduled for tomorrow
        List<TrainingPlan> tomorrowTrainings = trainingPlanRepository.findByDate(tomorrow);
        
        for (TrainingPlan training : tomorrowTrainings) {
            Team team = training.getTeam();
            User coach = team.getOwner();
            
            // Send reminder to coach
            sendCoachReminder(coach, training, team);
            
            // Send reminders to team players (if implemented)
            sendPlayerReminders(training, team);
        }
        
        log.info("Completed training reminder notifications job. Sent {} reminders", tomorrowTrainings.size());
    }
    
    @Scheduled(cron = "0 0 9 * * 1") // Every Monday at 9:00 AM
    public void sendWeeklyDigest() {
        log.info("Starting weekly digest job");
        
        LocalDate startOfWeek = LocalDate.now();
        LocalDate endOfWeek = startOfWeek.plusDays(6);
        
        // Find all active teams
        List<Team> activeTeams = teamRepository.findByActiveTrue();
        
        for (Team team : activeTeams) {
            User coach = team.getOwner();
            
            // Find all training plans for this team in the current week
            List<TrainingPlan> weeklyTrainings = trainingPlanRepository.findByTeamAndDateBetween(
                    team, startOfWeek, endOfWeek);
            
            if (!weeklyTrainings.isEmpty()) {
                sendWeeklyDigestEmail(coach, team, weeklyTrainings, startOfWeek, endOfWeek);
            }
        }
        
        log.info("Completed weekly digest job for {} teams", activeTeams.size());
    }
    
    @Scheduled(cron = "0 0 0 1 * ?") // First day of each month at midnight
    public void sendMonthlyUsageReport() {
        log.info("Starting monthly usage report job");
        
        // Find all users with admin role
        List<User> adminUsers = userRepository.findByRolesName("ROLE_ADMIN");
        
        // Generate and send usage report to admins
        for (User admin : adminUsers) {
            sendMonthlyUsageReportEmail(admin);
        }
        
        log.info("Completed monthly usage report job for {} admins", adminUsers.size());
    }
    
    private void sendCoachReminder(User coach, TrainingPlan training, Team team) {
        try {
            Map<String, Object> templateModel = new HashMap<>();
            templateModel.put("coachName", coach.getFirstName());
            templateModel.put("teamName", team.getName());
            templateModel.put("trainingTitle", training.getTitle());
            templateModel.put("trainingDate", training.getDate().toString());
            templateModel.put("trainingTime", training.getStartTime() != null ? training.getStartTime().toString() : "Not specified");
            templateModel.put("trainingDuration", training.getDuration() + " minutes");
            
            emailService.sendEmail(
                    coach.getEmail(),
                    "Training Reminder: " + team.getName() + " - Tomorrow",
                    "training-reminder",
                    templateModel);
            
            log.info("Sent training reminder to coach {} for team {}", coach.getUsername(), team.getName());
        } catch (Exception e) {
            log.error("Failed to send training reminder to coach {}", coach.getUsername(), e);
        }
    }
    
    private void sendPlayerReminders(TrainingPlan training, Team team) {
        // Implementation for sending reminders to players
        // This would typically involve getting all players from the team
        // and sending them individual emails
        log.info("Player reminders not implemented yet");
    }
    
    private void sendWeeklyDigestEmail(User coach, Team team, List<TrainingPlan> trainings, 
                                      LocalDate startDate, LocalDate endDate) {
        try {
            Map<String, Object> templateModel = new HashMap<>();
            templateModel.put("coachName", coach.getFirstName());
            templateModel.put("teamName", team.getName());
            templateModel.put("weekStart", startDate.toString());
            templateModel.put("weekEnd", endDate.toString());
            templateModel.put("trainings", trainings);
            templateModel.put("trainingCount", trainings.size());
            
            emailService.sendEmail(
                    coach.getEmail(),
                    "Weekly Training Schedule: " + team.getName(),
                    "weekly-digest",
                    templateModel);
            
            log.info("Sent weekly digest to coach {} for team {}", coach.getUsername(), team.getName());
        } catch (Exception e) {
            log.error("Failed to send weekly digest to coach {}", coach.getUsername(), e);
        }
    }
    
    private void sendMonthlyUsageReportEmail(User admin) {
        try {
            // Here you would typically gather usage statistics
            // such as new users, active teams, storage usage, etc.
            
            Map<String, Object> templateModel = new HashMap<>();
            templateModel.put("adminName", admin.getFirstName());
            templateModel.put("month", LocalDateTime.now().minusMonths(1).getMonth().toString());
            templateModel.put("year", LocalDateTime.now().getYear());
            // Add usage statistics to the template model
            
            emailService.sendEmail(
                    admin.getEmail(),
                    "TacticBoard Monthly Usage Report",
                    "monthly-report",
                    templateModel);
            
            log.info("Sent monthly usage report to admin {}", admin.getUsername());
        } catch (Exception e) {
            log.error("Failed to send monthly usage report to admin {}", admin.getUsername(), e);
        }
    }
}