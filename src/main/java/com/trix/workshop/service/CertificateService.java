package com.trix.workshop.service;

import com.trix.workshop.certificate.PdfCertificateGenerator;
import com.trix.workshop.dto.CertificateDto;
import com.trix.workshop.dto.CertificateEligibilityDto;
import com.trix.workshop.entity.*;
import com.trix.workshop.exception.BusinessRuleException;
import com.trix.workshop.exception.ResourceNotFoundException;
import com.trix.workshop.exception.UnauthorizedException;
import com.trix.workshop.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
public class CertificateService {
    private final CertificateRepository certificateRepository;
    private final RegistrationRepository registrationRepository;
    private final AttendanceRepository attendanceRepository;
    private final WorkshopRepository workshopRepository;
    private final UserRepository userRepository;
    private final PdfCertificateGenerator pdfGenerator;
    private final AuditService auditService;

    public CertificateService(CertificateRepository certificateRepository,
                              RegistrationRepository registrationRepository,
                              AttendanceRepository attendanceRepository,
                              WorkshopRepository workshopRepository,
                              UserRepository userRepository,
                              PdfCertificateGenerator pdfGenerator,
                              AuditService auditService) {
        this.certificateRepository = certificateRepository;
        this.registrationRepository = registrationRepository;
        this.attendanceRepository = attendanceRepository;
        this.workshopRepository = workshopRepository;
        this.userRepository = userRepository;
        this.pdfGenerator = pdfGenerator;
        this.auditService = auditService;
    }

    public List<CertificateEligibilityDto> getEligibilityList(Long workshopId) {
        workshopRepository.findById(workshopId)
                .orElseThrow(() -> new ResourceNotFoundException("Workshop not found with ID: " + workshopId));
        return certificateRepository.findEligibilityByWorkshop(workshopId);
    }

    @Transactional
    public CertificateDto generateCertificateForRegistration(Long registrationId, User admin) {
        Registration reg = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new ResourceNotFoundException("Registration not found with ID: " + registrationId));

        // 1. Verify eligibility
        if (!"APPROVED".equalsIgnoreCase(reg.getRegistrationStatus())) {
            throw new BusinessRuleException("Cannot generate certificate: Registration status is " + reg.getRegistrationStatus() + " (must be APPROVED)");
        }

        Optional<Attendance> attOpt = attendanceRepository.findByRegistrationId(registrationId);
        if (attOpt.isEmpty() || !"PRESENT".equalsIgnoreCase(attOpt.get().getAttendanceStatus())) {
            String curr = attOpt.map(Attendance::getAttendanceStatus).orElse("NOT_RECORDED");
            throw new BusinessRuleException("Cannot generate certificate: Attendance status is " + curr + " (must be PRESENT)");
        }

        // 2. Check duplicate
        Optional<Certificate> existing = certificateRepository.findByRegistrationId(registrationId);
        if (existing.isPresent()) {
            throw new BusinessRuleException("Certificate already generated for this registration (Certificate ID: " + existing.get().getCertificateNumber() + ")");
        }

        // 3. Create Certificate entity
        String certNumber = generateCertificateNumber();
        String verCode = generateVerificationCode();

        Certificate cert = new Certificate();
        cert.setRegistrationId(registrationId);
        cert.setParticipantId(reg.getParticipantId());
        cert.setWorkshopId(reg.getWorkshopId());
        cert.setCertificateNumber(certNumber);
        cert.setVerificationCode(verCode);
        cert.setCertificateStatus("GENERATED");
        cert.setEligibilityVerified(true);
        cert.setIssueDate(LocalDate.now());

        cert = certificateRepository.save(cert);

        // 4. Generate PDF file
        CertificateDto dto = certificateRepository.findDtoById(cert.getCertificateId())
                .orElseThrow(() -> new ResourceNotFoundException("Failed to load certificate record"));

        try {
            pdfGenerator.generateCertificatePdf(dto);
        } catch (Exception e) {
            throw new BusinessRuleException("Failed to render PDF certificate: " + e.getMessage());
        }

        auditService.log(admin.getEmail(), "GENERATE_CERTIFICATE", "CERTIFICATE", cert.getCertificateId(),
                "Generated certificate " + certNumber + " for registration " + registrationId);

        return dto;
    }

    @Transactional
    public Map<String, Object> bulkGenerateCertificates(Long workshopId, User admin) {
        Workshop w = workshopRepository.findById(workshopId)
                .orElseThrow(() -> new ResourceNotFoundException("Workshop not found with ID: " + workshopId));

        List<CertificateEligibilityDto> eligibilityList = certificateRepository.findEligibilityByWorkshop(workshopId);

        int generatedCount = 0;
        int skippedCount = 0;

        for (CertificateEligibilityDto item : eligibilityList) {
            // Only generate if ELIGIBLE and not yet generated
            if ("ELIGIBLE".equalsIgnoreCase(item.getEligibilityStatus()) && item.getCertificateId() == null) {
                try {
                    generateCertificateForRegistration(item.getRegistrationId(), admin);
                    generatedCount++;
                } catch (Exception e) {
                    skippedCount++;
                }
            } else {
                skippedCount++;
            }
        }

        auditService.log(admin.getEmail(), "BULK_GENERATE_CERTIFICATES", "WORKSHOP", workshopId,
                "Bulk generated " + generatedCount + " certificates for workshop: " + w.getTitle());

        Map<String, Object> result = new HashMap<>();
        result.put("generatedCount", generatedCount);
        result.put("skippedCount", skippedCount);
        result.put("totalEligibleChecked", eligibilityList.size());
        return result;
    }

    @Transactional
    public void issueCertificate(Long certificateId, User admin) {
        Certificate cert = certificateRepository.findById(certificateId)
                .orElseThrow(() -> new ResourceNotFoundException("Certificate not found with ID: " + certificateId));

        certificateRepository.updateStatus(certificateId, "ISSUED");

        auditService.log(admin.getEmail(), "ISSUE_CERTIFICATE", "CERTIFICATE", certificateId,
                "Issued certificate " + cert.getCertificateNumber() + " to participant");
    }

    @Transactional
    public void issueAllForWorkshop(Long workshopId, User admin) {
        List<CertificateDto> certs = certificateRepository.findAll(workshopId, "GENERATED");
        for (CertificateDto c : certs) {
            certificateRepository.updateStatus(c.getCertificateId(), "ISSUED");
        }
        auditService.log(admin.getEmail(), "BULK_ISSUE_CERTIFICATES", "WORKSHOP", workshopId,
                "Issued " + certs.size() + " certificates for workshop ID: " + workshopId);
    }

    public List<CertificateDto> getMyCertificates(Long participantId) {
        return certificateRepository.findByParticipantId(participantId);
    }

    public List<CertificateDto> getAllCertificates(Long workshopId, String status) {
        return certificateRepository.findAll(workshopId, status);
    }

    public CertificateDto getCertificateById(Long certificateId) {
        return certificateRepository.findDtoById(certificateId)
                .orElseThrow(() -> new ResourceNotFoundException("Certificate not found with ID: " + certificateId));
    }

    public byte[] getCertificatePdfBytes(Long certificateId, User currentUser) {
        CertificateDto dto = certificateRepository.findDtoById(certificateId)
                .orElseThrow(() -> new ResourceNotFoundException("Certificate not found with ID: " + certificateId));

        // Security check: Only admin or the certificate's owner participant can download
        if (!"ADMIN".equalsIgnoreCase(currentUser.getRole()) && !dto.getParticipantId().equals(currentUser.getUserId())) {
            throw new UnauthorizedException("You are not authorized to download this certificate.");
        }

        try {
            return pdfGenerator.generateCertificatePdf(dto);
        } catch (Exception e) {
            throw new BusinessRuleException("Could not generate PDF download: " + e.getMessage());
        }
    }

    public CertificateDto verifyCertificateByCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            throw new BusinessRuleException("Please provide a valid certificate number or verification code.");
        }
        return certificateRepository.findByVerificationCode(code.trim().toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("No valid certificate found matching: " + code.trim()));
    }

    private String generateCertificateNumber() {
        int year = LocalDate.now().getYear();
        String randomHex = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
        return "TRIX-CERT-" + year + "-" + randomHex;
    }

    private String generateVerificationCode() {
        String randomHex = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        return "V-" + randomHex;
    }
}

