package com.trix.workshop.entity;

import java.time.LocalDateTime;

public class User {
    private Long userId;
    private String fullName;
    private String email;
    private String passwordHash;
    private String role; // 'ADMIN' or 'PARTICIPANT'
    private String phone;
    private String department;
    private String institution;
    private String accountStatus; // 'ACTIVE' or 'SUSPENDED'
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public User() {}

    public User(Long userId, String fullName, String email, String passwordHash, String role,
                String phone, String department, String institution, String accountStatus,
                LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.phone = phone;
        this.department = department;
        this.institution = institution;
        this.accountStatus = accountStatus;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getInstitution() { return institution; }
    public void setInstitution(String institution) { this.institution = institution; }

    public String getAccountStatus() { return accountStatus; }
    public void setAccountStatus(String accountStatus) { this.accountStatus = accountStatus; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

