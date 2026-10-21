-- Workshop Registration & Certificate Issue System
-- Group TRIX | KTU B.Tech S3 Computer Science and Engineering Java Mini Project
-- Database Schema for MySQL 8.0+

CREATE DATABASE IF NOT EXISTS workshop_management
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE workshop_management;

-- 1. USERS TABLE
CREATE TABLE IF NOT EXISTS users (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(150) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('ADMIN', 'PARTICIPANT') NOT NULL DEFAULT 'PARTICIPANT',
    phone VARCHAR(25),
    department VARCHAR(100),
    institution VARCHAR(150),
    account_status ENUM('ACTIVE', 'SUSPENDED') NOT NULL DEFAULT 'ACTIVE',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_email (email),
    INDEX idx_user_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. WORKSHOPS TABLE
CREATE TABLE IF NOT EXISTS workshops (
    workshop_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    category VARCHAR(100) NOT NULL,
    organizer_name VARCHAR(150) NOT NULL,
    organizer_id BIGINT NULL,
    workshop_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    venue VARCHAR(150) NOT NULL,
    capacity INT NOT NULL,
    registration_deadline DATE NOT NULL,
    status ENUM('UPCOMING', 'ONGOING', 'COMPLETED', 'CANCELLED') NOT NULL DEFAULT 'UPCOMING',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_workshop_date (workshop_date),
    INDEX idx_workshop_category (category),
    INDEX idx_workshop_status (status),
    CONSTRAINT fk_workshop_organizer FOREIGN KEY (organizer_id) REFERENCES users(user_id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. REGISTRATIONS TABLE
CREATE TABLE IF NOT EXISTS registrations (
    registration_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    participant_id BIGINT NOT NULL,
    workshop_id BIGINT NOT NULL,
    registration_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    registration_status ENUM('PENDING', 'APPROVED', 'REJECTED', 'CANCELLED') NOT NULL DEFAULT 'APPROVED',
    notes VARCHAR(255),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_participant_workshop (participant_id, workshop_id),
    INDEX idx_reg_participant (participant_id),
    INDEX idx_reg_workshop (workshop_id),
    INDEX idx_reg_status (registration_status),
    CONSTRAINT fk_reg_participant FOREIGN KEY (participant_id) REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT fk_reg_workshop FOREIGN KEY (workshop_id) REFERENCES workshops(workshop_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. ATTENDANCE TABLE
CREATE TABLE IF NOT EXISTS attendance (
    attendance_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    registration_id BIGINT NOT NULL UNIQUE,
    workshop_id BIGINT NOT NULL,
    participant_id BIGINT NOT NULL,
    attendance_status ENUM('PRESENT', 'ABSENT', 'NOT_RECORDED') NOT NULL DEFAULT 'NOT_RECORDED',
    recorded_at DATETIME NULL,
    recorded_by VARCHAR(150) NULL,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_att_workshop (workshop_id),
    INDEX idx_att_participant (participant_id),
    INDEX idx_att_status (attendance_status),
    CONSTRAINT fk_att_registration FOREIGN KEY (registration_id) REFERENCES registrations(registration_id) ON DELETE CASCADE,
    CONSTRAINT fk_att_workshop FOREIGN KEY (workshop_id) REFERENCES workshops(workshop_id) ON DELETE CASCADE,
    CONSTRAINT fk_att_participant FOREIGN KEY (participant_id) REFERENCES users(user_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 5. CERTIFICATES TABLE
CREATE TABLE IF NOT EXISTS certificates (
    certificate_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    registration_id BIGINT NOT NULL UNIQUE,
    participant_id BIGINT NOT NULL,
    workshop_id BIGINT NOT NULL,
    certificate_number VARCHAR(64) NOT NULL UNIQUE,
    verification_code VARCHAR(32) NOT NULL UNIQUE,
    certificate_status ENUM('GENERATED', 'ISSUED', 'REVOKED') NOT NULL DEFAULT 'GENERATED',
    eligibility_verified BOOLEAN NOT NULL DEFAULT TRUE,
    issue_date DATE NOT NULL,
    file_path VARCHAR(255) NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_cert_number (certificate_number),
    INDEX idx_cert_verification (verification_code),
    INDEX idx_cert_participant (participant_id),
    INDEX idx_cert_workshop (workshop_id),
    CONSTRAINT fk_cert_registration FOREIGN KEY (registration_id) REFERENCES registrations(registration_id) ON DELETE CASCADE,
    CONSTRAINT fk_cert_participant FOREIGN KEY (participant_id) REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT fk_cert_workshop FOREIGN KEY (workshop_id) REFERENCES workshops(workshop_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 6. AUDIT LOGS TABLE
CREATE TABLE IF NOT EXISTS audit_logs (
    log_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    actor_email VARCHAR(150) NOT NULL,
    action VARCHAR(100) NOT NULL,
    entity_type VARCHAR(50) NOT NULL,
    entity_id BIGINT NULL,
    details TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_audit_actor (actor_email),
    INDEX idx_audit_action (action),
    INDEX idx_audit_created (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

