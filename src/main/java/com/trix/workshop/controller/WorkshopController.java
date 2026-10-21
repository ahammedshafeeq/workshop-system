package com.trix.workshop.controller;

import com.trix.workshop.dto.ApiResponse;
import com.trix.workshop.dto.WorkshopDto;
import com.trix.workshop.dto.WorkshopRequest;
import com.trix.workshop.entity.User;
import com.trix.workshop.service.AuthService;
import com.trix.workshop.service.WorkshopService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workshops")
public class WorkshopController {
    private final WorkshopService workshopService;
    private final AuthService authService;

    public WorkshopController(WorkshopService workshopService, AuthService authService) {
        this.workshopService = workshopService;
        this.authService = authService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<WorkshopDto>>> getAllWorkshops(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search,
            HttpSession session) {
        Long currentUserId = null;
        try {
            User user = authService.getSessionUser(session);
            currentUserId = user.getUserId();
        } catch (Exception ignored) {}

        List<WorkshopDto> list = workshopService.getAllWorkshops(category, status, search, currentUserId);
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<WorkshopDto>> getWorkshopById(@PathVariable Long id, HttpSession session) {
        Long currentUserId = null;
        try {
            User user = authService.getSessionUser(session);
            currentUserId = user.getUserId();
        } catch (Exception ignored) {}

        WorkshopDto dto = workshopService.getWorkshopById(id, currentUserId);
        return ResponseEntity.ok(ApiResponse.success(dto));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<WorkshopDto>> createWorkshop(
            @Valid @RequestBody WorkshopRequest request,
            HttpSession session) {
        authService.requireAdmin(session);
        User admin = authService.getSessionUser(session);
        WorkshopDto created = workshopService.createWorkshop(request, admin);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Workshop created successfully", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<WorkshopDto>> updateWorkshop(
            @PathVariable Long id,
            @Valid @RequestBody WorkshopRequest request,
            HttpSession session) {
        authService.requireAdmin(session);
        User admin = authService.getSessionUser(session);
        WorkshopDto updated = workshopService.updateWorkshop(id, request, admin);
        return ResponseEntity.ok(ApiResponse.success("Workshop updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteWorkshop(
            @PathVariable Long id,
            HttpSession session) {
        authService.requireAdmin(session);
        User admin = authService.getSessionUser(session);
        workshopService.deleteWorkshop(id, admin);
        return ResponseEntity.ok(ApiResponse.success("Workshop deleted successfully", null));
    }
}

