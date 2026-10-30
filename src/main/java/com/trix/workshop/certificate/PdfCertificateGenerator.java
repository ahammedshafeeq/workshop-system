package com.trix.workshop.certificate;

import com.lowagie.text.DocumentException;
import com.trix.workshop.certificate.engine.CertificatePdfExporter;
import com.trix.workshop.certificate.engine.CertificateRenderer;
import com.trix.workshop.certificate.model.CertificateData;
import com.trix.workshop.certificate.template.CertificateTemplate;
import com.trix.workshop.certificate.template.TemplateRegistry;
import com.trix.workshop.dto.CertificateDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;

@Component
public class PdfCertificateGenerator {
    private static final Logger log = LoggerFactory.getLogger(PdfCertificateGenerator.class);

    @Value("${app.institution.name:APJ Abdul Kalam Technological University}")
    private String institutionName;

    @Value("${app.department.name:Department of Computer Science and Engineering}")
    private String departmentName;

    @Value("${app.signatory.hod:Dr. Anand Varma, Ph.D.}")
    private String hodName;

    @Value("${app.signatory.coordinator:Prof. Lakshmi Priya, M.Tech}")
    private String coordinatorName;

    @Value("${app.certificate.storage-dir:./certificates}")
    private String storageDir;

    private final CertificatePdfExporter pdfExporter = new CertificatePdfExporter();

    public byte[] generateCertificatePdf(CertificateDto cert) throws DocumentException, IOException {
        return generateCertificatePdf(cert, "classic_gold");
    }

    public byte[] generateCertificatePdf(CertificateDto cert, String templateId) throws DocumentException, IOException {
        CertificateTemplate template = TemplateRegistry.getTemplateOrDefault(templateId);

        CertificateData data = new CertificateData();
        data.setRecipientName(cert.getParticipantName());
        data.setWorkshopTitle(cert.getWorkshopTitle());
        data.setWorkshopDate(cert.getWorkshopDate());
        data.setVenueOrMode(cert.getVenue());
        data.setOrganizerName(institutionName + " - " + departmentName);
        data.setCertificateCode(cert.getCertificateNumber() != null ? cert.getCertificateNumber() : "TRIX-2026-001");
        data.setCertificateType(CertificateData.CertificateType.fromString(cert.getCertificateStatus()));
        data.setIssueDate(cert.getIssueDate() != null ? cert.getIssueDate() : LocalDate.now());
        data.setSignatory1Name(hodName);
        data.setSignatory1Title("Head of Department");
        data.setSignatory2Name(coordinatorName);
        data.setSignatory2Title("Faculty Coordinator");

        String verCode = cert.getVerificationCode() != null ? cert.getVerificationCode() : data.getCertificateCode();
        data.setVerificationUrl("http://localhost:8080/verify.html?code=" + verCode);

        // Render at 300 DPI (3508 x 2480)
        BufferedImage masterImage = CertificateRenderer.render(template, data);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        pdfExporter.exportToPdfStream(masterImage, data, baos);
        byte[] pdfBytes = baos.toByteArray();

        // Save to disk storage folder
        saveToStorage(cert.getCertificateNumber(), pdfBytes);

        return pdfBytes;
    }

    private void saveToStorage(String certNumber, byte[] bytes) {
        if (certNumber == null) return;
        try {
            File dir = new File(storageDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(dir, certNumber + ".pdf");
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(bytes);
            }
        } catch (Exception e) {
            log.warn("Could not write certificate PDF to disk: {}", e.getMessage());
        }
    }
}
