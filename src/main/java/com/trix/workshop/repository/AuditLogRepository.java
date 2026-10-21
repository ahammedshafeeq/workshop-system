package com.trix.workshop.repository;

import com.trix.workshop.entity.AuditLog;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AuditLogRepository {
    private final JdbcTemplate jdbcTemplate;

    public AuditLogRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<AuditLog> auditLogRowMapper = (rs, rowNum) -> new AuditLog(
            rs.getLong("log_id"),
            rs.getString("actor_email"),
            rs.getString("action"),
            rs.getString("entity_type"),
            rs.getObject("entity_id") != null ? rs.getLong("entity_id") : null,
            rs.getString("details"),
            rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null
    );

    public void save(AuditLog log) {
        String sql = "INSERT INTO audit_logs (actor_email, action, entity_type, entity_id, details) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, log.getActorEmail(), log.getAction(), log.getEntityType(), log.getEntityId(), log.getDetails());
    }

    public List<AuditLog> findRecent(int limit) {
        String sql = "SELECT * FROM audit_logs ORDER BY created_at DESC LIMIT ?";
        return jdbcTemplate.query(sql, auditLogRowMapper, limit);
    }
}

