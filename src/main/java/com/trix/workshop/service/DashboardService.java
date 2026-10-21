package com.trix.workshop.service;

import com.trix.workshop.dto.DashboardStatsDto;
import com.trix.workshop.dto.WorkshopDto;
import com.trix.workshop.entity.AuditLog;
import com.trix.workshop.entity.Workshop;
import com.trix.workshop.repository.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DashboardService {
    private final WorkshopRepository workshopRepository;
    private final RegistrationRepository registrationRepository;
    private final AttendanceRepository attendanceRepository;
    private final CertificateRepository certificateRepository;
    private final UserRepository userRepository;
    private final AuditService auditService;
    private final JdbcTemplate jdbcTemplate;

    public DashboardService(WorkshopRepository workshopRepository,
                            RegistrationRepository registrationRepository,
                            AttendanceRepository attendanceRepository,
                            CertificateRepository certificateRepository,
                            UserRepository userRepository,
                            AuditService auditService,
                            JdbcTemplate jdbcTemplate) {
        this.workshopRepository = workshopRepository;
        this.registrationRepository = registrationRepository;
        this.attendanceRepository = attendanceRepository;
        this.certificateRepository = certificateRepository;
        this.userRepository = userRepository;
        this.auditService = auditService;
        this.jdbcTemplate = jdbcTemplate;
    }

    public DashboardStatsDto getAdminDashboardStats() {
        DashboardStatsDto stats = new DashboardStatsDto();

        stats.setTotalWorkshops(workshopRepository.countAll());
        stats.setUpcomingWorkshops(workshopRepository.countByStatus("UPCOMING"));
        stats.setOngoingWorkshops(workshopRepository.countByStatus("ONGOING"));
        stats.setCompletedWorkshops(workshopRepository.countByStatus("COMPLETED"));
        stats.setTotalParticipants(userRepository.countAllParticipants());

        stats.setPendingRegistrations(registrationRepository.countByStatus("PENDING"));
        stats.setApprovedRegistrations(registrationRepository.countByStatus("APPROVED"));
        stats.setRejectedRegistrations(registrationRepository.countByStatus("REJECTED"));
        stats.setCancelledRegistrations(registrationRepository.countByStatus("CANCELLED"));

        int presentCount = attendanceRepository.countByStatus("PRESENT");
        int absentCount = attendanceRepository.countByStatus("ABSENT");
        int totalRecorded = presentCount + absentCount;
        double attendanceRate = totalRecorded > 0 ? ((double) presentCount / totalRecorded) * 100.0 : 0.0;

        stats.setTotalPresent(presentCount);
        stats.setTotalAbsent(absentCount);
        stats.setOverallAttendanceRate(Math.round(attendanceRate * 10.0) / 10.0);

        stats.setCertificatesIssued(certificateRepository.countByStatus("ISSUED"));
        stats.setCertificatesGenerated(certificateRepository.countAll());

        // Registration status breakdown
        Map<String, Integer> regMap = new HashMap<>();
        regMap.put("APPROVED", stats.getApprovedRegistrations());
        regMap.put("PENDING", stats.getPendingRegistrations());
        regMap.put("REJECTED", stats.getRejectedRegistrations());
        regMap.put("CANCELLED", stats.getCancelledRegistrations());
        stats.setRegistrationStatusBreakdown(regMap);

        // Category breakdown
        Map<String, Integer> catMap = new HashMap<>();
        String catSql = "SELECT category, COUNT(*) as cnt FROM workshops GROUP BY category";
        jdbcTemplate.query(catSql, (rs, rowNum) -> {
            catMap.put(rs.getString("category"), rs.getInt("cnt"));
            return null;
        });
        stats.setCategoryBreakdown(catMap);

        // Upcoming workshops (top 5)
        List<Workshop> upcoming = workshopRepository.findUpcoming(5);
        List<WorkshopDto> dtoList = upcoming.stream().map(w -> {
            WorkshopDto dto = new WorkshopDto();
            dto.setWorkshopId(w.getWorkshopId());
            dto.setTitle(w.getTitle());
            dto.setCategory(w.getCategory());
            dto.setWorkshopDate(w.getWorkshopDate());
            dto.setStartTime(w.getStartTime());
            dto.setEndTime(w.getEndTime());
            dto.setVenue(w.getVenue());
            dto.setCapacity(w.getCapacity());
            dto.setStatus(w.getStatus());
            int reg = workshopRepository.getRegisteredCount(w.getWorkshopId());
            dto.setRegisteredCount(reg);
            dto.setAvailableSeats(Math.max(0, w.getCapacity() - reg));
            return dto;
        }).collect(Collectors.toList());
        stats.setUpcomingWorkshopsList(dtoList);

        // Recent audit activities (top 10)
        stats.setRecentActivities(auditService.getRecentLogs(10));

        return stats;
    }

    public DashboardStatsDto getParticipantDashboardStats(Long participantId) {
        DashboardStatsDto stats = new DashboardStatsDto();

        int registeredCount = registrationRepository.countByParticipant(participantId);
        stats.setParticipantRegisteredCount(registeredCount);

        // Count upcoming workshops the user registered for
        String upcomingSql = "SELECT COUNT(*) FROM registrations r " +
                             "JOIN workshops w ON r.workshop_id = w.workshop_id " +
                             "WHERE r.participant_id = ? AND r.registration_status = 'APPROVED' " +
                             "AND w.workshop_date >= CURRENT_DATE";
        Integer upcoming = jdbcTemplate.queryForObject(upcomingSql, Integer.class, participantId);
        stats.setParticipantUpcomingCount(upcoming != null ? upcoming : 0);

        // Completed workshops
        String compSql = "SELECT COUNT(*) FROM registrations r " +
                         "JOIN workshops w ON r.workshop_id = w.workshop_id " +
                         "WHERE r.participant_id = ? AND (w.workshop_date < CURRENT_DATE OR w.status = 'COMPLETED')";
        Integer comp = jdbcTemplate.queryForObject(compSql, Integer.class, participantId);
        stats.setParticipantCompletedCount(comp != null ? comp : 0);

        // Certificates available
        stats.setParticipantCertificatesCount(certificateRepository.countByParticipant(participantId));

        return stats;
    }
}

