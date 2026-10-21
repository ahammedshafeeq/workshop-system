package com.trix.workshop;

import com.trix.workshop.certificate.PdfCertificateGenerator;
import com.trix.workshop.dto.CertificateDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class PdfCertificateGeneratorTest {

    @Autowired
    private PdfCertificateGenerator pdfGenerator;

    @Test
    @DisplayName("Verify that PDF certificate generator produces a non-empty valid PDF byte array")
    void testPdfGeneration() throws Exception {
        CertificateDto dto = new CertificateDto();
        dto.setCertificateNumber("TRIX-CERT-2026-TEST01");
        dto.setVerificationCode("V-TEST99");
        dto.setParticipantName("Test Student");
        dto.setWorkshopTitle("Test Cloud Microservices Workshop");
        dto.setWorkshopDate("2026-10-25");
        dto.setVenue("Turing Hall");
        dto.setIssueDate(LocalDate.now());

        byte[] pdfBytes = pdfGenerator.generateCertificatePdf(dto);

        assertNotNull(pdfBytes, "PDF byte array must not be null");
        assertTrue(pdfBytes.length > 500, "PDF byte array must be a valid non-empty file");
        // Check standard PDF magic header '%PDF-'
        assertEquals('%', (char) pdfBytes[0]);
        assertEquals('P', (char) pdfBytes[1]);
        assertEquals('D', (char) pdfBytes[2]);
        assertEquals('F', (char) pdfBytes[3]);
        assertEquals('-', (char) pdfBytes[4]);
    }
}

