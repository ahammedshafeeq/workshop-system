package com.trix.workshop.controller;

import com.trix.workshop.dto.ApiResponse;
import com.trix.workshop.dto.CertificateDto;
import com.trix.workshop.dto.CertificateEligibilityDto;
import com.trix.workshop.entity.User;
import com.trix.workshop.service.AuthService;
import com.trix.workshop.service.CertificateService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class CertificateController {
    private final CertificateService certificateService;
    private final AuthService authService;

    public CertificateController(CertificateService certificateService, AuthService authService) {
        this.certificateService = certificateService;
        this.authService = authService;
    }

    @GetMapping("/certificates/my")
    public ResponseEntity<ApiResponse<List<CertificateDto>>> getMyCertificates(HttpSession session) {
        User participant = authService.getSessionUser(session);
        List<CertificateDto> list = certificateService.getMyCertificates(participant.getUserId());
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @GetMapping("/certificates")
    public ResponseEntity<ApiResponse<List<CertificateDto>>> getAllCertificates(
            @RequestParam(required = false) Long workshopId,
            @RequestParam(required = false) String status,
            HttpSession session) {
        authService.requireAdmin(session);
        List<CertificateDto> list = certificateService.getAllCertificates(workshopId, status);
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @GetMapping("/certificates/workshop/{id}/eligibility")
    public ResponseEntity<ApiResponse<List<CertificateEligibilityDto>>> getWorkshopEligibility(
            @PathVariable Long id,
            HttpSession session) {
        authService.requireAdmin(session);
        List<CertificateEligibilityDto> list = certificateService.getEligibilityList(id);
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @PostMapping("/registrations/{id}/certificate")
    public ResponseEntity<ApiResponse<CertificateDto>> generateCertificate(
            @PathVariable Long id,
            HttpSession session) {
        authService.requireAdmin(session);
        User admin = authService.getSessionUser(session);
        CertificateDto dto = certificateService.generateCertificateForRegistration(id, admin);
        return ResponseEntity.ok(ApiResponse.success("Certificate generated successfully!", dto));
    }

    @PostMapping("/workshops/{id}/certificates/bulk-generate")
    public ResponseEntity<ApiResponse<Map<String, Object>>> bulkGenerateCertificates(
            @PathVariable Long id,
            HttpSession session) {
        authService.requireAdmin(session);
        User admin = authService.getSessionUser(session);
        Map<String, Object> result = certificateService.bulkGenerateCertificates(id, admin);
        return ResponseEntity.ok(ApiResponse.success("Bulk certificate generation completed", result));
    }

    @PostMapping("/certificates/{id}/issue")
    public ResponseEntity<ApiResponse<Void>> issueCertificate(
            @PathVariable Long id,
            HttpSession session) {
        authService.requireAdmin(session);
        User admin = authService.getSessionUser(session);
        certificateService.issueCertificate(id, admin);
        return ResponseEntity.ok(ApiResponse.success("Certificate issued to participant", null));
    }

    @PostMapping("/workshops/{id}/certificates/issue-all")
    public ResponseEntity<ApiResponse<Void>> issueAllForWorkshop(
            @PathVariable Long id,
            HttpSession session) {
        authService.requireAdmin(session);
        User admin = authService.getSessionUser(session);
        certificateService.issueAllForWorkshop(id, admin);
        return ResponseEntity.ok(ApiResponse.success("All generated certificates have been issued", null));
    }

    @GetMapping("/certificates/{id}/download")
    public ResponseEntity<byte[]> downloadCertificate(
            @PathVariable Long id,
            HttpSession session) {
        User currentUser = authService.getSessionUser(session);
        byte[] pdfBytes = certificateService.getCertificatePdfBytes(id, currentUser);
        CertificateDto dto = certificateService.getCertificateById(id);

        String filename = "Certificate-" + dto.getCertificateNumber() + ".pdf";

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(pdfBytes);
    }

    // Public Certificate Verification Endpoint
    @GetMapping("/certificates/verify/{code}")
    public ResponseEntity<ApiResponse<CertificateDto>> verifyCertificate(@PathVariable String code) {
        CertificateDto dto = certificateService.verifyCertificateByCode(code);
        // Cleanse private details before public response
        CertificateDto publicDto = new CertificateDto();
        publicDto.setCertificateNumber(dto.getCertificateNumber());
        publicDto.setVerificationCode(dto.getVerificationCode());
        publicDto.setParticipantName(dto.getParticipantName());
        publicDto.setWorkshopTitle(dto.getWorkshopTitle());
        publicDto.setWorkshopDate(dto.getWorkshopDate());
        publicDto.setVenue(dto.getVenue());
        publicDto.setIssueDate(dto.getIssueDate());
        publicDto.setCertificateStatus(dto.getCertificateStatus());
        publicDto.setEligibilityVerified(dto.getEligibilityVerified());

        return ResponseEntity.ok(ApiResponse.success("Certificate verified successfully", publicDto));
    }
}

