package com.trix.workshop.controller;

import com.trix.workshop.dto.ApiResponse;
import com.trix.workshop.entity.AuditLog;
import com.trix.workshop.service.AuditService;
import com.trix.workshop.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/audit-logs")
public class AuditController {
    private final AuditService auditService;
    private final AuthService authService;

    public AuditController(AuditService auditService, AuthService authService) {
        this.auditService = auditService;
        this.authService = authService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AuditLog>>> getRecentLogs(
            @RequestParam(defaultValue = "50") int limit,
            HttpSession session) {
        authService.requireAdmin(session);
        List<AuditLog> list = auditService.getRecentLogs(limit);
        return ResponseEntity.ok(ApiResponse.success(list));
    }
}

