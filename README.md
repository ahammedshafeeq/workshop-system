# Workshop Registration & Certificate Issue System

**Platform Brand:** TRIX Workshops  
**Academic Context:** KTU B.Tech S3 Computer Science and Engineering Java Mini Project  
**Development Group:** Group TRIX  
**Department:** Department of Computer Science and Engineering  
**Theme:** Premium Teal & White Enterprise Design System (`#F8FAFC` / `#FFFFFF` / Primary `#0D9488` / Deep `#0F766E` / Midnight `#042F2E`)

---

## 1. Project Overview & Architecture

The **Workshop Registration & Certificate Issue System** is an enterprise-grade, fully functional web application designed to automate the entire workshop lifecycle across university departments:
1. **Workshop Creation & Management:** Full CRUD, capacity limits, registration deadlines, category tagging, and venue allocation.
2. **Participant Registration:** Self-registration, capacity constraints, real-time seat availability calculation, and duplicate enrollment prevention.
3. **Attendance Management:** Workshop participant rosters with live attendance percentage calculations and Present/Absent/Reset marking.
4. **Automated Certificate Eligibility Engine:** Real-time evaluation enforcing academic rules: **Registration Status = APPROVED** AND **Attendance = PRESENT**.
5. **PDF Certificate Generation:** Server-side generation of high-resolution landscape A4 completion certificates with university crest, unique Certificate IDs (`TRIX-CERT-2026-XXXXX`), and Verification Codes (`V-XXXXXX`) using **OpenPDF**.
6. **Public Certificate Verification:** Standalone verification portal allowing recruiters and institutions to validate certificate authenticity without authentication.
7. **Reports & Sanitized CSV Export:** 5 comprehensive analytical reports with defense against CSV Formula Injection.

```
       Browser (HTML5, CSS3 Light Theme, JavaScript, Chart.js)
                                  │
                                  ▼
           Spring Boot 3.3.4 (Java 21 LTS REST API & MVC)
                                  │
              ┌───────────────────┴───────────────────┐
              ▼                                       ▼
    Security & Business Logic               OpenPDF Engine
  (BCrypt, Role Guards, Sessions)        (Landscape Certificates)
              │
              ▼
   Spring JDBC (JdbcTemplate & PreparedStatements & Transactions)
              │
              ▼
    MySQL 8.0 Database (Normalized InnoDB with Foreign Keys & Constraints)
```

---

## 2. Technology Stack

- **Backend:** Java 21 LTS, Spring Boot 3.3.4, Spring MVC REST Controllers, Spring Security Crypto (`BCryptPasswordEncoder`), OpenPDF 2.0.3.
- **Data Access:** Spring JDBC (`JdbcTemplate`), HikariCP Connection Pool, MySQL Connector/J 8.3.0.
- **Database:** MySQL Server 8.0+, InnoDB Storage Engine, UTF8MB4 Unicode.
- **Frontend:** HTML5, CSS3 (Strict Light Design System), Modern Vanilla JavaScript (Fetch API, DOM manipulation), Chart.js 4.4.2.
- **Build System:** Apache Maven 3.9+.

---

## 3. Database Schema

The relational schema is configured in `database/schema.sql`:

1. `users`: Stores administrator and participant accounts, hashed credentials, contact, department, and institution.
2. `workshops`: Workshop records including date, schedule, venue, capacity, deadline, and status.
3. `registrations`: Participant enrollments with unique constraint on `(participant_id, workshop_id)`.
4. `attendance`: Participation records linked to each registration with audit fields (`recorded_at`, `recorded_by`).
5. `certificates`: Generated certificate records with unique `certificate_number` and `verification_code`.
6. `audit_logs`: Audit trail tracking operations, actors, timestamps, and target entities.

---

## 4. Default Demo Accounts

| Role | Email | Password | Access Privileges |
| :--- | :--- | :--- | :--- |
| **Administrator** | `admin@trix.edu` | `Admin@123` | Full dashboard, create/edit workshops, attendance, bulk certificates, CSV reports |
| **Participant 1** | `rahul.nair@trix.edu` | `Rahul@123` | Browse workshops, enroll, check attendance, download issued PDF certificate |
| **Participant 2** | `sneha.menon@trix.edu` | `Sneha@123` | Browse workshops, view registered sessions |
| **Participant 3** | `arjun.das@trix.edu` | `Arjun@123` | Present in Workshop 4; ready for demo certificate generation |

---

## 5. Quick Start & Execution Guide (Windows)

### Prerequisites:
- Java Development Kit (JDK 17 or JDK 21 LTS)
- Apache Maven 3.8+
- MySQL Server 8.0 running on `localhost:3306`

### Step 1: Database Initialization
Open **PowerShell** or **Command Prompt** and run:
```powershell
# Log in to MySQL and initialize the database schema and sample data:
& "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -u root -proot -e "CREATE DATABASE IF NOT EXISTS workshop_management;"
& "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -u root -proot workshop_management -e "source database/schema.sql;"
```

### Step 2: Configure Credentials
Verify database settings in `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/workshop_management?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true&characterEncoding=UTF-8
spring.datasource.username=root
spring.datasource.password=root
```

### Step 3: Compile & Run the Application
From the project root:
```powershell
cd C:\Users\FARIH\.gemini\antigravity\scratch\workshop-system

# Option A: Run directly with Spring Boot Maven plugin
mvn spring-boot:run

# Option B: Run the packaged JAR
mvn clean package
java -jar target/workshop-system-1.0.0.jar
```

### Step 4: Access the Application
Open your web browser and navigate to:
- **Public Portal / Landing Page:** [http://localhost:8080/](http://localhost:8080/)
- **Sign In Page:** [http://localhost:8080/login.html](http://localhost:8080/login.html)
- **Public Certificate Verification:** [http://localhost:8080/verify.html](http://localhost:8080/verify.html)

---

## 6. End-to-End Workflow Demonstration Guide

### A. Administrator Demonstration
1. Open [http://localhost:8080/login.html](http://localhost:8080/login.html) and click the **Administrator** 1-click login button (`admin@trix.edu` / `Admin@123`).
2. **Dashboard:** View top metric cards (Total Workshops, Upcoming, Total Participants, Pending Registrations, Overall Attendance %, Certificates Issued). Inspect the interactive Category bar chart and Registration Status doughnut chart.
3. **Workshop Management (`workshops.html`):** Click **New Workshop**, create a session with date, capacity, and deadline, and observe validation (e.g. End Time > Start Time).
4. **Attendance Tracking (`attendance.html`):** Select **"Hands-on Full-Stack Web Development with React & Java"**. View live attendance percentage. Toggle attendance status for participants (Present, Absent, Reset).
5. **Automated Certificate Engine (`certificates.html`):**
   - Select **Workshop 4**.
   - Notice the Eligibility column dynamically calculated:
     - Participants who are **APPROVED** + **PRESENT** are marked **ELIGIBLE**.
     - Participants marked **ABSENT** are marked **NOT ELIGIBLE** with reason.
   - Click **"Bulk Generate for All Eligible"** to generate genuine PDFs in a single transaction.
   - Click **"Issue All Generated"** or single **"Issue"** to release certificates to participants.
6. **Reports (`reports.html`):** Explore the 5 reports, apply filters, and click **Export Active Report (CSV)**.

### B. Participant Demonstration
1. Sign in as Participant (`rahul.nair@trix.edu` / `Rahul@123`).
2. **Dashboard (`participant-dashboard.html`):** View registered workshops, attendance status, and issued certificates.
3. **Download Certificate:** Click **Download PDF** to obtain the authentic, landscape A4 certificate file rendered by OpenPDF.
4. **Browse & Register (`workshops.html`):** Browse available workshops, open details, and register for an upcoming session. Observe real-time seat decrement.
5. **Cancel Registration:** From the participant dashboard, cancel an enrolled seat and observe seat capacity release.

### C. Public Verification Demonstration
1. Open [http://localhost:8080/verify.html](http://localhost:8080/verify.html).
2. Enter verification code: `V-TRIX01` or certificate number `TRIX-CERT-2026-KTU001`.
3. View the authenticated credentials badge with recipient name, workshop title, venue, and university endorsement.

---

## 7. Security Implementation

- **Password Security:** Salted BCrypt hashing via `BCryptPasswordEncoder` (strength 10). Plaintext passwords are never stored.
- **Role-Based Authorization:** Server-side verification on all `/api/**` endpoints. Client URL tampering is rejected with HTTP 401/403.
- **SQL Injection Prevention:** 100% parameterized SQL via Spring `JdbcTemplate` and `PreparedStatement`.
- **CSV Formula Injection Prevention:** Data fields prefixed with formula symbols (`=`, `+`, `-`, `@`) are escaped before export.
- **Session Security:** `HttpOnly`, `SameSite=Strict` cookie protection.
- **Download Authorization:** Only administrators or the certificate owner can download the certificate PDF.

---

## 8. Viva Presentation Q&A Points

1. **Why JdbcTemplate instead of JPA/Hibernate?**  
   *JdbcTemplate provides direct, transparent control over SQL queries, transactions, and performance without Hibernate overhead, making parameterized queries explicit and easier to defend during academic evaluation.*
2. **How is certificate eligibility calculated?**  
   *The eligibility engine verifies that the registration status is `APPROVED` and the attendance status is `PRESENT`. Participants who are Absent or unrecorded are barred from certificate issuance.*
3. **How does concurrency prevention work?**  
   *Transactions with database constraints (`UNIQUE(participant_id, workshop_id)`) and atomic capacity checks prevent race conditions and overbooking.*

---
*Developed by Group TRIX for KTU B.Tech S3 Computer Science and Engineering.*

