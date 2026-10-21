package com.trix.workshop.entity;

import java.time.LocalDateTime;

public class AuditLog {
    private Long logId;
    private String actorEmail;
    private String action;
    private String entityType;
    private Long entityId;
    private String details;
    private LocalDateTime createdAt;

    public AuditLog() {}

    public AuditLog(Long logId, String actorEmail, String action, String entityType,
                    Long entityId, String details, LocalDateTime createdAt) {
        this.logId = logId;
        this.actorEmail = actorEmail;
        this.action = action;
        this.entityType = entityType;
        this.entityId = entityId;
        this.details = details;
        this.createdAt = createdAt;
    }

    public AuditLog(String actorEmail, String action, String entityType, Long entityId, String details) {
        this.actorEmail = actorEmail;
        this.action = action;
        this.entityType = entityType;
        this.entityId = entityId;
        this.details = details;
        this.createdAt = LocalDateTime.now();
    }

    public Long getLogId() { return logId; }
    public void setLogId(Long logId) { this.logId = logId; }

    public String getActorEmail() { return actorEmail; }
    public void setActorEmail(String actorEmail) { this.actorEmail = actorEmail; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getEntityType() { return entityType; }
    public void setEntityType(String entityType) { this.entityType = entityType; }

    public Long getEntityId() { return entityId; }
    public void setEntityId(Long entityId) { this.entityId = entityId; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

