package com.trix.workshop.certificate.service;

import com.trix.workshop.certificate.dao.CertificateDAO;
import com.trix.workshop.certificate.engine.CertificatePdfExporter;
import com.trix.workshop.certificate.engine.CertificateRenderer;
import com.trix.workshop.certificate.model.CertificateData;
import com.trix.workshop.certificate.template.CertificateTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.awt.image.BufferedImage;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;

/**
 * Core business orchestrator verifying attendance, generating collision-free codes,
 * persisting to DB, and invoking rendering and PDF/PNG export.
 */
public class PremiumCertificateService {
    private static final Logger log = LoggerFactory.getLogger(PremiumCertificateService.class);

    private final DataSource dataSource;
    private final CertificateDAO certificateDAO;
    private final CertificateRenderer renderer;
    private final CertificatePdfExporter pdfExporter;
    private final String storageDirectory;

    public PremiumCertificateService(DataSource dataSource) {
        this(dataSource, "./certificates");
    }

    public PremiumCertificateService(DataSource dataSource, String storageDirectory) {
        this.dataSource = dataSource;
        this.certificateDAO = new CertificateDAO();
        this.renderer = new CertificateRenderer();
        this.pdfExporter = new CertificatePdfExporter();
        this.storageDirectory = storageDirectory != null ? storageDirectory : "./certificates";

        File dir = new File(this.storageDirectory);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    /**
     * Issues a certificate for a given registration.
     * Enforces attendance eligibility, generates code, renders image, and writes PDF/PNG files.
     */
    public CertificateData issueCertificate(long registrationId,
                                           CertificateTemplate template,
                                           CertificateData.CertificateType overrideType,
                                           Integer overrideSentenceVariant,
                                           Long adminId) throws Exception {

        try (Connection con = dataSource.getConnection()) {
            // 1. Verify Registration and Attendance
            String querySql = "SELECT r.registration_id, r.participant_id, r.workshop_id, r.registration_status, " +
                              "u.full_name, u.email, " +
                              "w.title, w.workshop_date, w.venue, w.organizer_name, " +
                              "COALESCE(a.attendance_status, 'NOT_RECORDED') as attendance_status " +
                              "FROM registrations r " +
                              "JOIN users u ON r.participant_id = u.user_id " +
                              "JOIN workshops w ON r.workshop_id = w.workshop_id " +
                              "LEFT JOIN attendance a ON r.registration_id = a.registration_id " +
                              "WHERE r.registration_id = ?";

            long participantId;
            long workshopId;
            String participantName;
            String workshopTitle;
            String workshopDate;
            String venue;
            String organizer;
            String attendanceStatus;

            try (PreparedStatement ps = con.prepareStatement(querySql)) {
                ps.setLong(1, registrationId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new IllegalArgumentException("Registration record #" + registrationId + " was not found in the system.");
                    }

                    String regStatus = rs.getString("registration_status");
                    if (!"APPROVED".equalsIgnoreCase(regStatus)) {
                        throw new IllegalStateException("Certificate issuance blocked: Registration status is '" + regStatus + "' (Must be APPROVED).");
                    }

                    attendanceStatus = rs.getString("attendance_status");
                    if (!"PRESENT".equalsIgnoreCase(attendanceStatus)) {
                        throw new IllegalStateException("Certificate issuance blocked: Attendance status is '" + attendanceStatus 
                                + "'. Participant must be marked PRESENT to receive a certificate.");
                    }

                    participantId = rs.getLong("participant_id");
                    workshopId = rs.getLong("workshop_id");
                    participantName = rs.getString("full_name");
                    workshopTitle = rs.getString("title");
                    workshopDate = rs.getString("workshop_date");
                    venue = rs.getString("venue");
                    organizer = rs.getString("organizer_name");
                }
            }

            // 2. Determine Certificate Type (100% attendance = Completion, partial = Participation, or Admin Override)
            CertificateData.CertificateType finalType;
            if (overrideType != null) {
                finalType = overrideType;
            } else if ("PRESENT".equalsIgnoreCase(attendanceStatus)) {
                finalType = CertificateData.CertificateType.COMPLETION;
            } else {
                finalType = CertificateData.CertificateType.PARTICIPATION;
            }

            // 3. Generate Guaranteed Unique Certificate Code: TRIX-YYYY-WWW-NNNN
            int currentYear = LocalDate.now().getYear();
            String certCode = certificateDAO.generateUniqueCertificateCode(con, currentYear, workshopId);

            // 4. Assemble CertificateData
            CertificateData certData = new CertificateData(
                    participantName,
                    workshopTitle,
                    workshopDate,
                    venue,
                    organizer,
                    certCode,
                    finalType,
                    LocalDate.now()
            );
            if (overrideSentenceVariant != null) {
                certData.setSentenceVariant(overrideSentenceVariant);
            }
            certData.setVerificationUrl("http://localhost:8080/verify.html?code=" + certCode);

            // 5. Render High-Resolution Print-Ready Graphics
            BufferedImage certImage = renderer.renderToImage(template, certData);

            // 6. Export to PDF and PNG in storage directory
            String safeCode = certCode.replace("-", "_");
            File pdfFile = new File(storageDirectory, safeCode + ".pdf");
            File pngFile = new File(storageDirectory, safeCode + ".png");

            renderer.exportToPng(certImage, pngFile);
            pdfExporter.exportToPdf(certImage, certData, pdfFile);

            // 7. Persist to Database via PreparedStatement
            certificateDAO.insertCertificateRecord(
                    con,
                    registrationId,
                    participantId,
                    workshopId,
                    certCode,
                    template.getId(),
                    finalType.name(),
                    adminId,
                    pdfFile.getAbsolutePath()
            );

            log.info("Successfully generated and issued certificate [{}] for [{}]", certCode, participantName);
            return certData;
        }
    }

    /**
     * Verifies a certificate by code.
     */
    public Optional<CertificateData> verifyCertificate(String code) {
        try (Connection con = dataSource.getConnection()) {
            return certificateDAO.findByCode(con, code);
        } catch (SQLException e) {
            log.error("Error verifying certificate: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public CertificateDAO getCertificateDAO() {
        return certificateDAO;
    }

    public CertificateRenderer getRenderer() {
        return renderer;
    }

    public CertificatePdfExporter getPdfExporter() {
        return pdfExporter;
    }
}
