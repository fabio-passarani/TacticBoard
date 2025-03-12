package com.tacticboard.persistence.service.impl;

import com.tacticboard.core.exception.ResourceNotFoundException;
import com.tacticboard.core.model.entity.notification.Notification;
import com.tacticboard.core.service.NotificationService;
import com.tacticboard.persistence.repository.NotificationRepository;
import com.tacticboard.persistence.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Autowired
    public NotificationServiceImpl(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Notification createNotification(Notification notification) {
        if (!userRepository.existsById(notification.getUser().getId())) {
            throw new ResourceNotFoundException("User", "id", notification.getUser().getId());
        }
        
        notification.setCreatedAt(LocalDateTime.now());
        notification.setRead(false);
        
        return notificationRepository.save(notification);
    }

    @Override
    public Notification markAsRead(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", id));
        
        notification.setRead(true);
        notification.setReadAt(LocalDateTime.now());
        
        return notificationRepository.save(notification);
    }

    @Override
    public void markAllAsRead(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }
        
        List<Notification> unreadNotifications = notificationRepository.findByUserIdAndIsReadFalse(userId);
        LocalDateTime now = LocalDateTime.now();
        
        for (Notification notification : unreadNotifications) {
            notification.setRead(true);
            notification.setReadAt(now);
        }
        
        notificationRepository.saveAll(unreadNotifications);
    }

    @Override
    public void deleteNotification(Long id) {
        if (!notificationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Notification", "id", id);
        }
        
        notificationRepository.deleteById(id);
    }

    @Override
    public Page<Notification> getUserNotifications(Long userId, Pageable pageable) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }
        
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    @Override
    public Page<Notification> getUnreadNotifications(Long userId, Pageable pageable) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }
        
        return notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId, pageable);
    }

    @Override
    public long countUnreadNotifications(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }
        
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }
    
    @Override
    public void deleteAllNotifications(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }
        
        notificationRepository.deleteByUserId(userId);
    }
    
    @Override
    public void deleteOldNotifications(Long userId, int daysOld) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }
        
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysOld);
        notificationRepository.deleteByUserIdAndCreatedAtBefore(userId, cutoffDate);
    }
    
    @Override
    public void sendTeamNotification(Long teamId, String title, String message, String type) {
        // Find all users associated with the team and send them notifications
        List<Long> userIds = userRepository.findUserIdsByTeamId(teamId);
        
        for (Long userId : userIds) {
            Notification notification = new Notification();
            notification.getUser().setId(userId);
            notification.setTitle(title);
            notification.setMessage(message);
            notification.setType(type);
            notification.setRelatedId(teamId);
            
            createNotification(notification);
        }
    }
    
    @Override
    public void sendSystemNotification(String title, String message) {
        // Send notification to all users
        List<Long> allUserIds = userRepository.findAllUserIds();
        
        for (Long userId : allUserIds) {
            Notification notification = new Notification();
            notification.getUser().setId(userId);
            notification.setTitle(title);
            notification.setMessage(message);
            notification.setType(Notification.NotificationType.SYSTEM.name());
            
            createNotification(notification);
        }
    }
}