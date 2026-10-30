package com.trix.workshop.repository;

import com.trix.workshop.entity.User;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository {
    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<User> userRowMapper = (rs, rowNum) -> new User(
            rs.getLong("user_id"),
            rs.getString("full_name"),
            rs.getString("email"),
            rs.getString("password_hash"),
            rs.getString("role"),
            rs.getString("phone"),
            rs.getString("department"),
            rs.getString("institution"),
            rs.getString("account_status"),
            rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null,
            rs.getTimestamp("updated_at") != null ? rs.getTimestamp("updated_at").toLocalDateTime() : null
    );

    public User save(User user) {
        String sql = "INSERT INTO users (full_name, email, password_hash, role, phone, department, institution, account_status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getFullName());
            ps.setString(2, user.getEmail().toLowerCase().trim());
            ps.setString(3, user.getPasswordHash());
            ps.setString(4, user.getRole() != null ? user.getRole() : "PARTICIPANT");
            ps.setString(5, user.getPhone());
            ps.setString(6, user.getDepartment());
            ps.setString(7, user.getInstitution());
            ps.setString(8, user.getAccountStatus() != null ? user.getAccountStatus() : "ACTIVE");
            return ps;
        }, keyHolder);

        Number key = extractKey(keyHolder, "user_id");
        if (key != null) {
            user.setUserId(key.longValue());
        } else {
            findByEmail(user.getEmail()).ifPresent(saved -> user.setUserId(saved.getUserId()));
        }
        return user;
    }

    private Number extractKey(KeyHolder keyHolder, String column) {
        if (keyHolder == null) return null;
        try {
            if (keyHolder.getKey() != null) return keyHolder.getKey();
        } catch (Exception ignored) {}
        try {
            if (keyHolder.getKeys() != null && !keyHolder.getKeys().isEmpty()) {
                for (java.util.Map.Entry<String, Object> entry : keyHolder.getKeys().entrySet()) {
                    if (entry.getKey().equalsIgnoreCase(column) && entry.getValue() instanceof Number) {
                        return (Number) entry.getValue();
                    }
                }
                for (Object val : keyHolder.getKeys().values()) {
                    if (val instanceof Number) {
                        return (Number) val;
                    }
                }
            }
        } catch (Exception ignored) {}
        return null;
    }

    public Optional<User> findById(Long userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        try {
            User user = jdbcTemplate.queryForObject(sql, userRowMapper, userId);
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE LOWER(email) = LOWER(?)";
        try {
            User user = jdbcTemplate.queryForObject(sql, userRowMapper, email.trim());
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public boolean existsByEmail(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE LOWER(email) = LOWER(?)";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email.trim());
        return count != null && count > 0;
    }

    public void update(User user) {
        String sql = "UPDATE users SET full_name = ?, phone = ?, department = ?, institution = ? WHERE user_id = ?";
        jdbcTemplate.update(sql, user.getFullName(), user.getPhone(), user.getDepartment(), user.getInstitution(), user.getUserId());
    }

    public List<User> findAllParticipants() {
        String sql = "SELECT * FROM users WHERE role = 'PARTICIPANT' ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, userRowMapper);
    }

    public int countAllParticipants() {
        String sql = "SELECT COUNT(*) FROM users WHERE role = 'PARTICIPANT'";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
        return count != null ? count : 0;
    }
}

