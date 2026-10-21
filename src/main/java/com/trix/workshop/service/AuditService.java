package com.trix.workshop.service;

import com.trix.workshop.entity.AuditLog;
import com.trix.workshop.repository.AuditLogRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuditService {
    private final AuditLogRepository auditLogRepository;

    public AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void log(String actorEmail, String action, String entityType, Long entityId, String details) {
        try {
            AuditLog log = new AuditLog(actorEmail != null ? actorEmail : "SYSTEM", action, entityType, entityId, details);
            auditLogRepository.save(log);
        } catch (Exception e) {
            // Audit logging failure should not crash the main transaction
        }
    }

    public List<AuditLog> getRecentLogs(int limit) {
        return auditLogRepository.findRecent(limit);
    }
}

