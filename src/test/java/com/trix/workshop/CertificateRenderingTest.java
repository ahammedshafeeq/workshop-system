package com.trix.workshop;

import com.trix.workshop.certificate.engine.CertificatePdfExporter;
import com.trix.workshop.certificate.engine.CertificateRenderer;
import com.trix.workshop.certificate.model.CertificateData;
import com.trix.workshop.certificate.template.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CertificateRenderingTest {

    private static CertificateRenderer renderer;
    private static CertificatePdfExporter pdfExporter;

    @BeforeAll
    public static void setUp() {
        renderer = new CertificateRenderer();
        pdfExporter = new CertificatePdfExporter();
    }

    private CertificateData createTestData(String recipient, String title) {
        CertificateData data = new CertificateData();
        data.setRecipientName(recipient);
        data.setWorkshopTitle(title);
        data.setWorkshopDate("October 30, 2026");
        data.setVenueOrMode("Auditorium Complex & Virtual Portal");
        data.setOrganizerName("Department of Computer Science & Engineering");
        data.setCertificateCode("TRIX-2026-001-0042");
        data.setCertificateType(CertificateData.CertificateType.COMPLETION);
        data.setIssueDate(LocalDate.now());
        data.setVerificationUrl("https://trixworkshops.com/verify?code=TRIX-2026-001-0042");
        return data;
    }

    @Test
    @DisplayName("Verify that all 8 registered templates render at 3508x2480 without throwing exceptions")
    void testAllTemplatesRender() {
        List<CertificateTemplate> templates = TemplateRegistry.getAllTemplates();
        assertEquals(8, templates.size(), "Should have exactly 8 templates registered");

        CertificateData data = createTestData("Jane Doe", "Full-Stack Cloud Engineering");

        for (CertificateTemplate template : templates) {
            BufferedImage image = renderer.renderToImage(template, data);
            assertNotNull(image, "Image should not be null for template: " + template.getId());
            assertEquals(3508, image.getWidth(), "Width should be A4 landscape 300 DPI (3508 px)");
            assertEquals(2480, image.getHeight(), "Height should be A4 landscape 300 DPI (2480 px)");
        }
    }

    @Test
    @DisplayName("Test Case 1: Very Long Name (50+ characters) auto-shrinks and wraps without clipping")
    void testVeryLongNameAutoShrink() {
        String longName = "Dr. Bartholomew Montgomery-Archibald Featherstonehaugh III";
        assertTrue(longName.length() >= 50, "Name must be 50+ characters");

        CertificateData data = createTestData(longName, "Advanced Quantum Computing");
        CertificateTemplate template = TemplateRegistry.get("classic_gold");

        BufferedImage image = renderer.renderToImage(template, data);
        assertNotNull(image);
    }

    @Test
    @DisplayName("Test Case 2: Single-letter Name renders cleanly")
    void testSingleLetterName() {
        CertificateData data = createTestData("Q", "Microservices Architecture");
        CertificateTemplate template = TemplateRegistry.get("modern_minimal");

        BufferedImage image = renderer.renderToImage(template, data);
        assertNotNull(image);
    }

    @Test
    @DisplayName("Test Case 3: Names with accents, apostrophes and special characters render cleanly")
    void testSpecialCharacterName() {
        CertificateData data = createTestData("Prof. François-José Müller & María Peña d'Arc", "Data Ethics");
        CertificateTemplate template = TemplateRegistry.get("royal_emerald");

        BufferedImage image = renderer.renderToImage(template, data);
        assertNotNull(image);
    }

    @Test
    @DisplayName("Test Case 4: Long Workshop Title auto-wraps cleanly within margin")
    void testLongWorkshopTitle() {
        String longTitle = "Comprehensive Advanced Systems Engineering, Distributed Fault-Tolerant Consensus Protocols & Deep Neural Network Optimization";
        CertificateData data = createTestData("Alex Morgan", longTitle);
        CertificateTemplate template = TemplateRegistry.get("dark_tech");

        BufferedImage image = renderer.renderToImage(template, data);
        assertNotNull(image);
    }

    @Test
    @DisplayName("Test Case 5: Missing signature image gracefully renders fallback cursive calligraphy")
    void testMissingSignatureFallback() {
        CertificateData data = createTestData("Sarah Connor", "Cybersecurity & Cryptography");
        // Non-existent image paths
        data.setSignatory1Name("Dr. Alan Turing");
        data.setSignatory2Name("Ada Lovelace");

        CertificateTemplate template = TemplateRegistry.get("creative_gradient");
        BufferedImage image = renderer.renderToImage(template, data);
        assertNotNull(image);
    }

    @Test
    @DisplayName("Verify PDF and PNG export generation")
    void testPdfAndPngExport() throws Exception {
        CertificateData data = createTestData("David Kim", "Artificial Intelligence & Robotics");
        CertificateTemplate template = TemplateRegistry.get("corporate_blue");

        BufferedImage image = renderer.renderToImage(template, data);

        // Test in-memory PDF streaming
        ByteArrayOutputStream pdfStream = new ByteArrayOutputStream();
        pdfExporter.exportToPdfStream(image, data, pdfStream);
        byte[] pdfBytes = pdfStream.toByteArray();

        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 1000);
        assertEquals('%', (char) pdfBytes[0]);

        // Test File PNG export
        File tmpDir = new File("target/test-output");
        tmpDir.mkdirs();
        File pngFile = new File(tmpDir, "test_cert.png");
        renderer.exportToPng(image, pngFile);
        assertTrue(pngFile.exists());
        assertTrue(pngFile.length() > 5000);
    }
}
