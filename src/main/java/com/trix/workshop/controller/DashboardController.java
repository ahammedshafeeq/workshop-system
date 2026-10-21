package com.trix.workshop.controller;

import com.trix.workshop.dto.ApiResponse;
import com.trix.workshop.dto.DashboardStatsDto;
import com.trix.workshop.entity.User;
import com.trix.workshop.service.AuthService;
import com.trix.workshop.service.DashboardService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    private final DashboardService dashboardService;
    private final AuthService authService;

    public DashboardController(DashboardService dashboardService, AuthService authService) {
        this.dashboardService = dashboardService;
        this.authService = authService;
    }

    @GetMapping("/admin")
    public ResponseEntity<ApiResponse<DashboardStatsDto>> getAdminDashboard(HttpSession session) {
        authService.requireAdmin(session);
        DashboardStatsDto stats = dashboardService.getAdminDashboardStats();
        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    @GetMapping("/participant")
    public ResponseEntity<ApiResponse<DashboardStatsDto>> getParticipantDashboard(HttpSession session) {
        User participant = authService.getSessionUser(session);
        DashboardStatsDto stats = dashboardService.getParticipantDashboardStats(participant.getUserId());
        return ResponseEntity.ok(ApiResponse.success(stats));
    }
}

