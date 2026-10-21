package com.trix.workshop.controller;

import com.trix.workshop.dto.ApiResponse;
import com.trix.workshop.dto.RegistrationDto;
import com.trix.workshop.entity.User;
import com.trix.workshop.service.AuthService;
import com.trix.workshop.service.RegistrationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class RegistrationController {
    private final RegistrationService registrationService;
    private final AuthService authService;

    public RegistrationController(RegistrationService registrationService, AuthService authService) {
        this.registrationService = registrationService;
        this.authService = authService;
    }

    @PostMapping("/workshops/{id}/registrations")
    public ResponseEntity<ApiResponse<RegistrationDto>> registerForWorkshop(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> body,
            HttpSession session) {
        User participant = authService.getSessionUser(session);
        String notes = body != null ? body.get("notes") : null;
        RegistrationDto dto = registrationService.registerParticipant(id, participant, notes);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Successfully registered for workshop!", dto));
    }

    @GetMapping("/registrations/my")
    public ResponseEntity<ApiResponse<List<RegistrationDto>>> getMyRegistrations(HttpSession session) {
        User participant = authService.getSessionUser(session);
        List<RegistrationDto> list = registrationService.getMyRegistrations(participant.getUserId());
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @GetMapping("/registrations")
    public ResponseEntity<ApiResponse<List<RegistrationDto>>> getAllRegistrations(
            @RequestParam(required = false) Long workshopId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search,
            HttpSession session) {
        authService.requireAdmin(session);
        List<RegistrationDto> list = registrationService.getAllRegistrations(workshopId, status, search);
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @PatchMapping("/registrations/{id}/status")
    public ResponseEntity<ApiResponse<Void>> updateRegistrationStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body,
            HttpSession session) {
        authService.requireAdmin(session);
        User admin = authService.getSessionUser(session);
        String status = body.get("status");
        registrationService.updateRegistrationStatus(id, status, admin);
        return ResponseEntity.ok(ApiResponse.success("Registration status updated to " + status, null));
    }

    @PostMapping("/registrations/{id}/cancel")
    public ResponseEntity<ApiResponse<Void>> cancelRegistration(
            @PathVariable Long id,
            HttpSession session) {
        User participant = authService.getSessionUser(session);
        registrationService.cancelRegistration(id, participant);
        return ResponseEntity.ok(ApiResponse.success("Registration cancelled successfully", null));
    }
}

