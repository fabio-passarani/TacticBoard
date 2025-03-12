package com.tacticboard.persistence.service.impl;

import com.tacticboard.core.exception.ResourceNotFoundException;
import com.tacticboard.core.model.entity.audit.AuditLog;
import com.tacticboard.core.service.AuditService;
import com.tacticboard.persistence.repository.AuditLogRepository;
import com.tacticboard.persistence.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class AuditServiceImpl implements AuditService {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    @Autowired
    public AuditServiceImpl(AuditLogRepository auditLogRepository, UserRepository userRepository) {
        this.auditLogRepository = auditLogRepository;
        this.userRepository = userRepository;
    }

    @Override
    public AuditLog logAction(AuditLog auditLog) {
        if (auditLog.getUser() != null && auditLog.getUser().getId() != null) {
            if (!userRepository.existsById(auditLog.getUser().getId())) {
                throw new ResourceNotFoundException("User", "id", auditLog.getUser().getId());
            }
        }
        
        auditLog.setTimestamp(LocalDateTime.now());
        return auditLogRepository.save(auditLog);
    }

    @Override
    public AuditLog logAction(String action, String entityType, Long entityId, Long userId, String details) {
        AuditLog auditLog = new AuditLog();
        auditLog.setAction(action);
        auditLog.setEntityType(entityType);
        auditLog.setEntityId(entityId);
        
        if (userId != null) {
            if (!userRepository.existsById(userId)) {
                throw new ResourceNotFoundException("User", "id", userId);
            }
            auditLog.getUser().setId(userId);
        }
        
        auditLog.setDetails(details);
        auditLog.setTimestamp(LocalDateTime.now());
        
        return auditLogRepository.save(auditLog);
    }

    @Override
    public Page<AuditLog> getAuditLogs(Pageable pageable) {
        return auditLogRepository.findAll(pageable);
    }

    @Override
    public Page<AuditLog> getAuditLogsByUser(Long userId, Pageable pageable) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }
        
        return auditLogRepository.findByUserId(userId, pageable);
    }

    @Override
    public Page<AuditLog> getAuditLogsByEntity(String entityType, Long entityId, Pageable pageable) {
        return auditLogRepository.findByEntityTypeAndEntityId(entityType, entityId, pageable);
    }

    @Override
    public Page<AuditLog> getAuditLogsByAction(String action, Pageable pageable) {
        return auditLogRepository.findByAction(action, pageable);
    }

    @Override
    public Page<AuditLog> getAuditLogsByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        return auditLogRepository.findByTimestampBetween(startDate, endDate, pageable);
    }

    @Override
    public void deleteOldAuditLogs(int daysToKeep) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysToKeep);
        auditLogRepository.deleteByTimestampBefore(cutoffDate);
    }
}