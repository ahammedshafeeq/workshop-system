package com.trix.workshop.service;

import com.trix.workshop.dto.AttendanceRecordRequest;
import com.trix.workshop.dto.AttendanceSummaryDto;
import com.trix.workshop.entity.Attendance;
import com.trix.workshop.entity.Registration;
import com.trix.workshop.entity.User;
import com.trix.workshop.entity.Workshop;
import com.trix.workshop.exception.BusinessRuleException;
import com.trix.workshop.exception.ResourceNotFoundException;
import com.trix.workshop.repository.AttendanceRepository;
import com.trix.workshop.repository.RegistrationRepository;
import com.trix.workshop.repository.WorkshopRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AttendanceService {
    private final AttendanceRepository attendanceRepository;
    private final RegistrationRepository registrationRepository;
    private final WorkshopRepository workshopRepository;
    private final AuditService auditService;

    public AttendanceService(AttendanceRepository attendanceRepository,
                             RegistrationRepository registrationRepository,
                             WorkshopRepository workshopRepository,
                             AuditService auditService) {
        this.attendanceRepository = attendanceRepository;
        this.registrationRepository = registrationRepository;
        this.workshopRepository = workshopRepository;
        this.auditService = auditService;
    }

    public List<Attendance> getAttendanceByWorkshop(Long workshopId) {
        Workshop w = workshopRepository.findById(workshopId)
                .orElseThrow(() -> new ResourceNotFoundException("Workshop not found with ID: " + workshopId));
        return attendanceRepository.findByWorkshopId(workshopId);
    }

    public AttendanceSummaryDto getAttendanceSummary(Long workshopId) {
        Workshop w = workshopRepository.findById(workshopId)
                .orElseThrow(() -> new ResourceNotFoundException("Workshop not found with ID: " + workshopId));
        AttendanceSummaryDto summary = attendanceRepository.getSummaryByWorkshopId(workshopId);
        summary.setWorkshopTitle(w.getTitle());
        return summary;
    }

    @Transactional
    public void recordAttendance(AttendanceRecordRequest request, User admin) {
        Registration reg = registrationRepository.findById(request.getRegistrationId())
                .orElseThrow(() -> new ResourceNotFoundException("Registration not found with ID: " + request.getRegistrationId()));

        if (!"APPROVED".equalsIgnoreCase(reg.getRegistrationStatus())) {
            throw new BusinessRuleException("Cannot mark attendance for registration with status: " + reg.getRegistrationStatus() + ". Only APPROVED registrations are eligible.");
        }

        String status = request.getStatus().toUpperCase().trim();
        if (!List.of("PRESENT", "ABSENT", "NOT_RECORDED").contains(status)) {
            throw new BusinessRuleException("Invalid attendance status: " + request.getStatus());
        }

        attendanceRepository.saveOrUpdate(
                reg.getRegistrationId(),
                reg.getWorkshopId(),
                reg.getParticipantId(),
                status,
                admin.getEmail()
        );

        auditService.log(admin.getEmail(), "RECORD_ATTENDANCE", "ATTENDANCE", reg.getRegistrationId(),
                "Marked attendance as " + status + " for registration ID: " + reg.getRegistrationId());
    }

    @Transactional
    public void recordBulkAttendance(Long workshopId, String defaultStatus, User admin) {
        Workshop w = workshopRepository.findById(workshopId)
                .orElseThrow(() -> new ResourceNotFoundException("Workshop not found with ID: " + workshopId));

        List<Attendance> list = attendanceRepository.findByWorkshopId(workshopId);
        for (Attendance a : list) {
            attendanceRepository.saveOrUpdate(
                    a.getRegistrationId(),
                    a.getWorkshopId(),
                    a.getParticipantId(),
                    defaultStatus,
                    admin.getEmail()
            );
        }

        auditService.log(admin.getEmail(), "BULK_ATTENDANCE", "WORKSHOP", workshopId,
                "Bulk marked all participants of workshop " + w.getTitle() + " as " + defaultStatus);
    }
}

