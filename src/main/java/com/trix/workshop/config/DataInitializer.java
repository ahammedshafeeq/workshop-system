package com.trix.workshop.config;

import com.trix.workshop.certificate.PdfCertificateGenerator;
import com.trix.workshop.dto.CertificateDto;
import com.trix.workshop.entity.*;
import com.trix.workshop.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final WorkshopRepository workshopRepository;
    private final RegistrationRepository registrationRepository;
    private final AttendanceRepository attendanceRepository;
    private final CertificateRepository certificateRepository;
    private final PasswordEncoder passwordEncoder;
    private final PdfCertificateGenerator pdfGenerator;

    public DataInitializer(UserRepository userRepository,
                           WorkshopRepository workshopRepository,
                           RegistrationRepository registrationRepository,
                           AttendanceRepository attendanceRepository,
                           CertificateRepository certificateRepository,
                           PasswordEncoder passwordEncoder,
                           PdfCertificateGenerator pdfGenerator) {
        this.userRepository = userRepository;
        this.workshopRepository = workshopRepository;
        this.registrationRepository = registrationRepository;
        this.attendanceRepository = attendanceRepository;
        this.certificateRepository = certificateRepository;
        this.passwordEncoder = passwordEncoder;
        this.pdfGenerator = pdfGenerator;
    }

    @Override
    public void run(String... args) {
        try {
            if (userRepository.findByEmail("admin@trix.edu").isEmpty()) {
                log.info("Initializing demo data for TRIX Workshop System...");
                seedData();
                log.info("Demo data initialization complete.");
            }
        } catch (Exception e) {
            log.error("DataInitializer error: ", e);
        }
    }

    private void seedData() {
        // 1. Create Admin
        User admin = new User();
        admin.setFullName("Dr. Anand Varma");
        admin.setEmail("admin@trix.edu");
        admin.setPasswordHash(passwordEncoder.encode("Admin@123"));
        admin.setRole("ADMIN");
        admin.setPhone("+91 94471 23456");
        admin.setDepartment("Computer Science and Engineering");
        admin.setInstitution("College of Engineering Trivandrum");
        admin.setAccountStatus("ACTIVE");
        admin = userRepository.save(admin);

        // 2. Create Participants
        User rahul = new User(null, "Rahul Nair", "rahul.nair@trix.edu", passwordEncoder.encode("Rahul@123"),
                "PARTICIPANT", "+91 98460 11223", "Computer Science and Engineering", "College of Engineering Trivandrum",
                "ACTIVE", null, null);
        rahul = userRepository.save(rahul);

        User sneha = new User(null, "Sneha Menon", "sneha.menon@trix.edu", passwordEncoder.encode("Sneha@123"),
                "PARTICIPANT", "+91 97455 33445", "Computer Science and Engineering", "Model Engineering College Kochi",
                "ACTIVE", null, null);
        sneha = userRepository.save(sneha);

        User arjun = new User(null, "Arjun Das", "arjun.das@trix.edu", passwordEncoder.encode("Arjun@123"),
                "PARTICIPANT", "+91 94960 55667", "Computer Science and Engineering", "TKM College of Engineering Kollam",
                "ACTIVE", null, null);
        arjun = userRepository.save(arjun);

        User anjali = new User(null, "Anjali Kumar", "anjali.kumar@trix.edu", passwordEncoder.encode("Anjali@123"),
                "PARTICIPANT", "+91 96330 77889", "Electronics and Communication", "Govt Engineering College Barton Hill",
                "ACTIVE", null, null);
        anjali = userRepository.save(anjali);

        User fathima = new User(null, "Fathima Zahra", "fathima.zahra@trix.edu", passwordEncoder.encode("Fathima@123"),
                "PARTICIPANT", "+91 95260 99001", "Artificial Intelligence & Data Science", "Rajagiri School of Engineering",
                "ACTIVE", null, null);
        fathima = userRepository.save(fathima);

        // 3. Create Workshops
        LocalDate today = LocalDate.now();

        // Workshop 1: Upcoming Cloud Computing
        Workshop w1 = new Workshop(null,
                "Mastering Cloud-Native Microservices with Spring Boot & Docker",
                "Comprehensive hands-on workshop covering RESTful architecture, containerization with Docker, API Gateway routing, distributed configuration, and resilient microservice communication patterns.",
                "Cloud Computing",
                "Prof. Lakshmi Priya",
                admin.getUserId(),
                today.plusDays(10),
                LocalTime.of(9, 30),
                LocalTime.of(16, 30),
                "Turing Auditorium, Main Block",
                30,
                today.plusDays(8),
                "UPCOMING",
                null, null);
        w1 = workshopRepository.save(w1);

        // Workshop 2: Upcoming AI
        Workshop w2 = new Workshop(null,
                "Applied Deep Learning & Computer Vision using PyTorch",
                "Explore convolutional neural networks, transfer learning with ResNet, object detection pipelines, and deploying vision models using ONNX and FastAPI.",
                "AI & Machine Learning",
                "Dr. Harikrishnan R",
                admin.getUserId(),
                today.plusDays(15),
                LocalTime.of(10, 0),
                LocalTime.of(16, 0),
                "Ada Lovelace Computing Lab",
                25,
                today.plusDays(12),
                "UPCOMING",
                null, null);
        w2 = workshopRepository.save(w2);

        // Workshop 3: Ongoing Cyber Security
        Workshop w3 = new Workshop(null,
                "Defensive Cyber Security & Ethical Hacking Essentials",
                "Practical laboratory exercises on OWASP Top 10 vulnerabilities, network packet analysis with Wireshark, secure authentication patterns, and penetration testing fundamentals.",
                "Cyber Security",
                "Er. Sanjay Varma",
                admin.getUserId(),
                today,
                LocalTime.of(9, 0),
                LocalTime.of(17, 0),
                "Seminar Hall B, CSE Block",
                40,
                today.minusDays(1),
                "ONGOING",
                null, null);
        w3 = workshopRepository.save(w3);

        // Workshop 4: Completed Web Development (For certificate demos)
        Workshop w4 = new Workshop(null,
                "Hands-on Full-Stack Web Development with React & Java",
                "End-to-end full stack architecture building responsive single-page applications, integrating Spring Boot REST APIs, JDBC database access, and secure JWT session workflows.",
                "Web Technologies",
                "Prof. Lakshmi Priya",
                admin.getUserId(),
                today.minusDays(5),
                LocalTime.of(9, 30),
                LocalTime.of(16, 30),
                "Central Computing Centre, Lab 3",
                35,
                today.minusDays(7),
                "COMPLETED",
                null, null);
        w4 = workshopRepository.save(w4);

        // Workshop 5: Completed IoT
        Workshop w5 = new Workshop(null,
                "Industrial IoT & Edge Intelligence with Raspberry Pi",
                "Interfacing digital sensors, MQTT communication protocols, cloud telemetry ingestion, and edge intelligence inference on embedded single-board computers.",
                "IoT & Embedded Systems",
                "Dr. George Mathew",
                admin.getUserId(),
                today.minusDays(12),
                LocalTime.of(10, 0),
                LocalTime.of(16, 30),
                "Embedded Systems Research Lab",
                20,
                today.minusDays(14),
                "COMPLETED",
                null, null);
        w5 = workshopRepository.save(w5);

        // 4. Create Registrations for Workshop 4 (Completed)
        // Rahul: Approved, Present, Certificate Issued
        Registration r1 = registrationRepository.save(new Registration(null, rahul.getUserId(), w4.getWorkshopId(), null, "APPROVED", "Confirmed seat", null, null));
        attendanceRepository.saveOrUpdate(r1.getRegistrationId(), w4.getWorkshopId(), rahul.getUserId(), "PRESENT", "Dr. Anand Varma");
        Certificate c1 = certificateRepository.save(new Certificate(null, r1.getRegistrationId(), rahul.getUserId(), w4.getWorkshopId(),
                "TRIX-CERT-2026-KTU001", "V-TRIX01", "ISSUED", true, today.minusDays(4), null, null, null));
        generatePdfQuietly(c1.getCertificateId());

        // Sneha: Approved, Present, Certificate Generated
        Registration r2 = registrationRepository.save(new Registration(null, sneha.getUserId(), w4.getWorkshopId(), null, "APPROVED", "Early registration", null, null));
        attendanceRepository.saveOrUpdate(r2.getRegistrationId(), w4.getWorkshopId(), sneha.getUserId(), "PRESENT", "Dr. Anand Varma");
        Certificate c2 = certificateRepository.save(new Certificate(null, r2.getRegistrationId(), sneha.getUserId(), w4.getWorkshopId(),
                "TRIX-CERT-2026-KTU002", "V-TRIX02", "GENERATED", true, today.minusDays(4), null, null, null));
        generatePdfQuietly(c2.getCertificateId());

        // Arjun: Approved, Present, Certificate NOT YET GENERATED (ready for demo!)
        Registration r3 = registrationRepository.save(new Registration(null, arjun.getUserId(), w4.getWorkshopId(), null, "APPROVED", "Confirmed seat", null, null));
        attendanceRepository.saveOrUpdate(r3.getRegistrationId(), w4.getWorkshopId(), arjun.getUserId(), "PRESENT", "Dr. Anand Varma");

        // Anjali: Approved, Absent (Ineligible reason: Absent)
        Registration r4 = registrationRepository.save(new Registration(null, anjali.getUserId(), w4.getWorkshopId(), null, "APPROVED", "Regular attendee", null, null));
        attendanceRepository.saveOrUpdate(r4.getRegistrationId(), w4.getWorkshopId(), anjali.getUserId(), "ABSENT", "Dr. Anand Varma");

        // Fathima: Approved, Not Recorded (Ineligible reason: Awaiting attendance)
        Registration r5 = registrationRepository.save(new Registration(null, fathima.getUserId(), w4.getWorkshopId(), null, "APPROVED", "Registered online", null, null));
        attendanceRepository.saveOrUpdate(r5.getRegistrationId(), w4.getWorkshopId(), fathima.getUserId(), "NOT_RECORDED", "SYSTEM_INIT");

        // Registrations for Workshop 3 (Ongoing)
        Registration r6 = registrationRepository.save(new Registration(null, rahul.getUserId(), w3.getWorkshopId(), null, "APPROVED", "Enrolled", null, null));
        attendanceRepository.saveOrUpdate(r6.getRegistrationId(), w3.getWorkshopId(), rahul.getUserId(), "NOT_RECORDED", "SYSTEM_INIT");

        Registration r7 = registrationRepository.save(new Registration(null, sneha.getUserId(), w3.getWorkshopId(), null, "APPROVED", "Enrolled", null, null));
        attendanceRepository.saveOrUpdate(r7.getRegistrationId(), w3.getWorkshopId(), sneha.getUserId(), "NOT_RECORDED", "SYSTEM_INIT");

        Registration r8 = registrationRepository.save(new Registration(null, arjun.getUserId(), w3.getWorkshopId(), null, "APPROVED", "Enrolled", null, null));
        attendanceRepository.saveOrUpdate(r8.getRegistrationId(), w3.getWorkshopId(), arjun.getUserId(), "NOT_RECORDED", "SYSTEM_INIT");

        // Registration for Workshop 1 (Upcoming)
        registrationRepository.save(new Registration(null, rahul.getUserId(), w1.getWorkshopId(), null, "APPROVED", "Seat reserved", null, null));
        registrationRepository.save(new Registration(null, anjali.getUserId(), w1.getWorkshopId(), null, "APPROVED", "Seat reserved", null, null));
    }

    private void generatePdfQuietly(Long certId) {
        try {
            CertificateDto dto = certificateRepository.findDtoById(certId).orElse(null);
            if (dto != null) {
                pdfGenerator.generateCertificatePdf(dto);
            }
        } catch (Exception e) {
            log.warn("Could not pre-render certificate PDF: {}", e.getMessage());
        }
    }
}

