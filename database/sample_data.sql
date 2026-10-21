-- Workshop Registration & Certificate Issue System
-- Group TRIX | KTU B.Tech S3 Computer Science and Engineering Java Mini Project
-- Seed Sample Data

USE workshop_management;

-- Clear existing data
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE audit_logs;
TRUNCATE TABLE certificates;
TRUNCATE TABLE attendance;
TRUNCATE TABLE registrations;
TRUNCATE TABLE workshops;
TRUNCATE TABLE users;
SET FOREIGN_KEY_CHECKS = 1;

-- 1. USERS (Password for admin: Admin@123, for participants: <Name>@123)
-- BCrypt encoded passwords:
-- Admin@123  -> $2a$10$wE2fIksbNfFv6L0Mv65r4.wWkZzNnZqR87vP6jI9qJ0yGg7Z9Lw2K
-- Rahul@123  -> $2a$10$0W9B6vQ4gR7e2J9bF5a0r.iZ5bJ3hK2lM1nO0pQ9rS8tU7vW6xY1Z
INSERT INTO users (user_id, full_name, email, password_hash, role, phone, department, institution, account_status)
VALUES
(1, 'Dr. Anand Varma', 'admin@trix.edu', '$2a$10$P2tE4Z6Y6P4XbQ7n0A6Mv.3F.rN5t2V8b6G4X3K9Q1A7B5C3D1E2F', 'ADMIN', '+91 94471 23456', 'Computer Science and Engineering', 'College of Engineering Trivandrum', 'ACTIVE'),
(2, 'Rahul Nair', 'rahul.nair@trix.edu', '$2a$10$P2tE4Z6Y6P4XbQ7n0A6Mv.3F.rN5t2V8b6G4X3K9Q1A7B5C3D1E2F', 'PARTICIPANT', '+91 98460 11223', 'Computer Science and Engineering', 'College of Engineering Trivandrum', 'ACTIVE'),
(3, 'Sneha Menon', 'sneha.menon@trix.edu', '$2a$10$P2tE4Z6Y6P4XbQ7n0A6Mv.3F.rN5t2V8b6G4X3K9Q1A7B5C3D1E2F', 'PARTICIPANT', '+91 97455 33445', 'Computer Science and Engineering', 'Model Engineering College Kochi', 'ACTIVE'),
(4, 'Arjun Das', 'arjun.das@trix.edu', '$2a$10$P2tE4Z6Y6P4XbQ7n0A6Mv.3F.rN5t2V8b6G4X3K9Q1A7B5C3D1E2F', 'PARTICIPANT', '+91 94960 55667', 'Computer Science and Engineering', 'TKM College of Engineering Kollam', 'ACTIVE'),
(5, 'Anjali Kumar', 'anjali.kumar@trix.edu', '$2a$10$P2tE4Z6Y6P4XbQ7n0A6Mv.3F.rN5t2V8b6G4X3K9Q1A7B5C3D1E2F', 'PARTICIPANT', '+91 96330 77889', 'Electronics and Communication', 'Govt Engineering College Barton Hill', 'ACTIVE'),
(6, 'Fathima Zahra', 'fathima.zahra@trix.edu', '$2a$10$P2tE4Z6Y6P4XbQ7n0A6Mv.3F.rN5t2V8b6G4X3K9Q1A7B5C3D1E2F', 'PARTICIPANT', '+91 95260 99001', 'Artificial Intelligence & Data Science', 'Rajagiri School of Engineering', 'ACTIVE');

-- 2. WORKSHOPS
INSERT INTO workshops (workshop_id, title, description, category, organizer_name, organizer_id, workshop_date, start_time, end_time, venue, capacity, registration_deadline, status)
VALUES
(1, 'Mastering Cloud-Native Microservices with Spring Boot & Docker', 'Comprehensive hands-on workshop covering RESTful architecture, containerization with Docker, API Gateway routing, and resilient communication.', 'Cloud Computing', 'Prof. Lakshmi Priya', 1, DATE_ADD(CURRENT_DATE, INTERVAL 10 DAY), '09:30:00', '16:30:00', 'Turing Auditorium, Main Block', 30, DATE_ADD(CURRENT_DATE, INTERVAL 8 DAY), 'UPCOMING'),
(2, 'Applied Deep Learning & Computer Vision using PyTorch', 'Explore convolutional neural networks, transfer learning with ResNet, object detection pipelines, and deploying vision models.', 'AI & Machine Learning', 'Dr. Harikrishnan R', 1, DATE_ADD(CURRENT_DATE, INTERVAL 15 DAY), '10:00:00', '16:00:00', 'Ada Lovelace Computing Lab', 25, DATE_ADD(CURRENT_DATE, INTERVAL 12 DAY), 'UPCOMING'),
(3, 'Defensive Cyber Security & Ethical Hacking Essentials', 'Practical laboratory exercises on OWASP Top 10 vulnerabilities, network packet analysis with Wireshark, and penetration testing fundamentals.', 'Cyber Security', 'Er. Sanjay Varma', 1, CURRENT_DATE, '09:00:00', '17:00:00', 'Seminar Hall B, CSE Block', 40, DATE_SUB(CURRENT_DATE, INTERVAL 1 DAY), 'ONGOING'),
(4, 'Hands-on Full-Stack Web Development with React & Java', 'End-to-end full stack architecture building responsive single-page applications, integrating Spring Boot REST APIs, and JDBC database access.', 'Web Technologies', 'Prof. Lakshmi Priya', 1, DATE_SUB(CURRENT_DATE, INTERVAL 5 DAY), '09:30:00', '16:30:00', 'Central Computing Centre, Lab 3', 35, DATE_SUB(CURRENT_DATE, INTERVAL 7 DAY), 'COMPLETED'),
(5, 'Industrial IoT & Edge Intelligence with Raspberry Pi', 'Interfacing digital sensors, MQTT communication protocols, cloud telemetry ingestion, and edge intelligence inference.', 'IoT & Embedded Systems', 'Dr. George Mathew', 1, DATE_SUB(CURRENT_DATE, INTERVAL 12 DAY), '10:00:00', '16:30:00', 'Embedded Systems Research Lab', 20, DATE_SUB(CURRENT_DATE, INTERVAL 14 DAY), 'COMPLETED');

-- 3. REGISTRATIONS
INSERT INTO registrations (registration_id, participant_id, workshop_id, registration_date, registration_status, notes)
VALUES
(1, 2, 4, DATE_SUB(NOW(), INTERVAL 6 DAY), 'APPROVED', 'Confirmed seat'),
(2, 3, 4, DATE_SUB(NOW(), INTERVAL 6 DAY), 'APPROVED', 'Early registration'),
(3, 4, 4, DATE_SUB(NOW(), INTERVAL 6 DAY), 'APPROVED', 'Confirmed seat'),
(4, 5, 4, DATE_SUB(NOW(), INTERVAL 6 DAY), 'APPROVED', 'Regular attendee'),
(5, 6, 4, DATE_SUB(NOW(), INTERVAL 6 DAY), 'APPROVED', 'Registered online'),
(6, 2, 3, DATE_SUB(NOW(), INTERVAL 2 DAY), 'APPROVED', 'Enrolled'),
(7, 3, 3, DATE_SUB(NOW(), INTERVAL 2 DAY), 'APPROVED', 'Enrolled'),
(8, 4, 3, DATE_SUB(NOW(), INTERVAL 2 DAY), 'APPROVED', 'Enrolled'),
(9, 2, 1, DATE_SUB(NOW(), INTERVAL 1 DAY), 'APPROVED', 'Seat reserved'),
(10, 5, 1, DATE_SUB(NOW(), INTERVAL 1 DAY), 'APPROVED', 'Seat reserved');

-- 4. ATTENDANCE
INSERT INTO attendance (attendance_id, registration_id, workshop_id, participant_id, attendance_status, recorded_at, recorded_by)
VALUES
(1, 1, 4, 2, 'PRESENT', DATE_SUB(NOW(), INTERVAL 5 DAY), 'Dr. Anand Varma'),
(2, 2, 4, 3, 'PRESENT', DATE_SUB(NOW(), INTERVAL 5 DAY), 'Dr. Anand Varma'),
(3, 3, 4, 4, 'PRESENT', DATE_SUB(NOW(), INTERVAL 5 DAY), 'Dr. Anand Varma'),
(4, 4, 4, 5, 'ABSENT',  DATE_SUB(NOW(), INTERVAL 5 DAY), 'Dr. Anand Varma'),
(5, 5, 4, 6, 'NOT_RECORDED', NULL, NULL),
(6, 6, 3, 2, 'NOT_RECORDED', NULL, NULL),
(7, 7, 3, 3, 'NOT_RECORDED', NULL, NULL),
(8, 8, 3, 4, 'NOT_RECORDED', NULL, NULL);

-- 5. CERTIFICATES
INSERT INTO certificates (certificate_id, registration_id, participant_id, workshop_id, certificate_number, verification_code, certificate_status, eligibility_verified, issue_date)
VALUES
(1, 1, 2, 4, 'TRIX-CERT-2026-KTU001', 'V-TRIX01', 'ISSUED', 1, DATE_SUB(CURRENT_DATE, INTERVAL 4 DAY)),
(2, 2, 3, 4, 'TRIX-CERT-2026-KTU002', 'V-TRIX02', 'GENERATED', 1, DATE_SUB(CURRENT_DATE, INTERVAL 4 DAY));

-- 6. AUDIT LOGS
INSERT INTO audit_logs (log_id, actor_email, action, entity_type, entity_id, details)
VALUES
(1, 'admin@trix.edu', 'CREATE_WORKSHOP', 'WORKSHOP', 4, 'Created workshop: Hands-on Full-Stack Web Development with React & Java'),
(2, 'rahul.nair@trix.edu', 'REGISTER_WORKSHOP', 'REGISTRATION', 1, 'Participant registered for workshop: Hands-on Full-Stack Web Development with React & Java'),
(3, 'admin@trix.edu', 'RECORD_ATTENDANCE', 'ATTENDANCE', 1, 'Marked attendance as PRESENT for registration ID: 1'),
(4, 'admin@trix.edu', 'GENERATE_CERTIFICATE', 'CERTIFICATE', 1, 'Generated certificate TRIX-CERT-2026-KTU001'),
(5, 'admin@trix.edu', 'ISSUE_CERTIFICATE', 'CERTIFICATE', 1, 'Issued certificate TRIX-CERT-2026-KTU001 to participant');

