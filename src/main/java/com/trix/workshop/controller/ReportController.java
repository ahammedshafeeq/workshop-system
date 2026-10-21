package com.trix.workshop.controller;

import com.trix.workshop.dto.ApiResponse;
import com.trix.workshop.dto.ReportFilterRequest;
import com.trix.workshop.service.AuthService;
import com.trix.workshop.service.ReportService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/reports")
public class ReportController {
    private final ReportService reportService;
    private final AuthService authService;

    public ReportController(ReportService reportService, AuthService authService) {
        this.reportService = reportService;
        this.authService = authService;
    }

    @GetMapping("/workshops")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getWorkshopSummaryReport(
            @ModelAttribute ReportFilterRequest filter,
            HttpSession session) {
        authService.requireAdmin(session);
        Map<String, Object> report = reportService.getWorkshopSummaryReport(filter);
        return ResponseEntity.ok(ApiResponse.success(report));
    }

    @GetMapping("/registrations")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getRegistrationReport(
            @ModelAttribute ReportFilterRequest filter,
            HttpSession session) {
        authService.requireAdmin(session);
        Map<String, Object> report = reportService.getRegistrationReport(filter);
        return ResponseEntity.ok(ApiResponse.success(report));
    }

    @GetMapping("/attendance")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAttendanceReport(
            @ModelAttribute ReportFilterRequest filter,
            HttpSession session) {
        authService.requireAdmin(session);
        Map<String, Object> report = reportService.getAttendanceReport(filter);
        return ResponseEntity.ok(ApiResponse.success(report));
    }

    @GetMapping("/certificates")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCertificateIssuanceReport(
            @ModelAttribute ReportFilterRequest filter,
            HttpSession session) {
        authService.requireAdmin(session);
        Map<String, Object> report = reportService.getCertificateIssuanceReport(filter);
        return ResponseEntity.ok(ApiResponse.success(report));
    }

    @GetMapping("/capacity")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCapacityReport(HttpSession session) {
        authService.requireAdmin(session);
        Map<String, Object> report = reportService.getCapacityReport();
        return ResponseEntity.ok(ApiResponse.success(report));
    }

    @GetMapping("/export/csv")
    public ResponseEntity<byte[]> exportCsv(
            @RequestParam String type,
            @ModelAttribute ReportFilterRequest filter,
            HttpSession session) {
        authService.requireAdmin(session);
        byte[] csvBytes = reportService.exportToCsv(type, filter);

        String filename = "report-" + type + "-" + System.currentTimeMillis() + ".csv";

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(csvBytes);
    }
}

