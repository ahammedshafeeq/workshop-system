package com.trix.workshop.controller;

import com.trix.workshop.dto.ApiResponse;
import com.trix.workshop.dto.AttendanceRecordRequest;
import com.trix.workshop.dto.AttendanceSummaryDto;
import com.trix.workshop.entity.Attendance;
import com.trix.workshop.entity.User;
import com.trix.workshop.service.AttendanceService;
import com.trix.workshop.service.AuthService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AttendanceController {
    private final AttendanceService attendanceService;
    private final AuthService authService;

    public AttendanceController(AttendanceService attendanceService, AuthService authService) {
        this.attendanceService = attendanceService;
        this.authService = authService;
    }

    @GetMapping("/workshops/{id}/attendance")
    public ResponseEntity<ApiResponse<List<Attendance>>> getAttendanceByWorkshop(
            @PathVariable Long id,
            HttpSession session) {
        authService.requireAdmin(session);
        List<Attendance> list = attendanceService.getAttendanceByWorkshop(id);
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @GetMapping("/workshops/{id}/attendance/summary")
    public ResponseEntity<ApiResponse<AttendanceSummaryDto>> getAttendanceSummary(
            @PathVariable Long id,
            HttpSession session) {
        authService.requireAdmin(session);
        AttendanceSummaryDto summary = attendanceService.getAttendanceSummary(id);
        return ResponseEntity.ok(ApiResponse.success(summary));
    }

    @PostMapping("/attendance/record")
    public ResponseEntity<ApiResponse<Void>> recordAttendance(
            @Valid @RequestBody AttendanceRecordRequest request,
            HttpSession session) {
        authService.requireAdmin(session);
        User admin = authService.getSessionUser(session);
        attendanceService.recordAttendance(request, admin);
        return ResponseEntity.ok(ApiResponse.success("Attendance recorded as " + request.getStatus(), null));
    }

    @PostMapping("/workshops/{id}/attendance/bulk")
    public ResponseEntity<ApiResponse<Void>> recordBulkAttendance(
            @PathVariable Long id,
            @RequestBody Map<String, String> body,
            HttpSession session) {
        authService.requireAdmin(session);
        User admin = authService.getSessionUser(session);
        String defaultStatus = body.getOrDefault("status", "PRESENT");
        attendanceService.recordBulkAttendance(id, defaultStatus, admin);
        return ResponseEntity.ok(ApiResponse.success("Bulk attendance updated to " + defaultStatus, null));
    }
}

