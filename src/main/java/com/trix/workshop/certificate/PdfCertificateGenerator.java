package com.trix.workshop.certificate;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.trix.workshop.dto.CertificateDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

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

    // Brand color palette
    private static final Color BRAND_PRIMARY = new Color(79, 70, 229);    // #4F46E5
    private static final Color BRAND_SECONDARY = new Color(99, 102, 241); // #6366F1
    private static final Color TEXT_DARK = new Color(17, 24, 39);         // #111827
    private static final Color TEXT_MUTED = new Color(100, 116, 139);     // #64748B
    private static final Color GOLD_ACCENT = new Color(217, 119, 6);      // #D97706
    private static final Color LIGHT_BG = new Color(247, 249, 252);       // #F7F9FC
    private static final Color BORDER_GRAY = new Color(229, 231, 235);    // #E5E7EB

    public byte[] generateCertificatePdf(CertificateDto cert) throws DocumentException, IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // Landscape A4: 842 x 595 points
        Document document = new Document(PageSize.A4.rotate(), 36, 36, 36, 36);
        PdfWriter writer = PdfWriter.getInstance(document, baos);

        document.open();

        PdfContentByte cb = writer.getDirectContent();

        // 1. Draw elegant outer and inner borders with corner accents
        drawBorders(cb);

        // 2. Header: Institution & Department
        Paragraph instPara = new Paragraph(institutionName.toUpperCase(),
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BRAND_PRIMARY));
        instPara.setAlignment(Element.ALIGN_CENTER);
        instPara.setSpacingBefore(18);
        document.add(instPara);

        Paragraph deptPara = new Paragraph(departmentName + "  •  Group TRIX",
                FontFactory.getFont(FontFactory.HELVETICA, 10, TEXT_MUTED));
        deptPara.setAlignment(Element.ALIGN_CENTER);
        deptPara.setSpacingAfter(10);
        document.add(deptPara);

        // 3. Title: CERTIFICATE OF COMPLETION
        Paragraph titlePara = new Paragraph("CERTIFICATE OF COMPLETION",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24, BRAND_PRIMARY));
        titlePara.setAlignment(Element.ALIGN_CENTER);
        document.add(titlePara);

        Paragraph subPara = new Paragraph("PROUDLY PRESENTED FOR ACADEMIC EXCELLENCE & PARTICIPATION",
                FontFactory.getFont(FontFactory.HELVETICA, 8, GOLD_ACCENT));
        subPara.setAlignment(Element.ALIGN_CENTER);
        subPara.setSpacingAfter(14);
        document.add(subPara);

        // 4. Statement
        Paragraph statement1 = new Paragraph("This is to certify that",
                FontFactory.getFont(FontFactory.HELVETICA, 11, TEXT_MUTED));
        statement1.setAlignment(Element.ALIGN_CENTER);
        statement1.setSpacingAfter(4);
        document.add(statement1);

        // 5. Participant Full Name
        Paragraph namePara = new Paragraph(cert.getParticipantName(),
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, TEXT_DARK));
        namePara.setAlignment(Element.ALIGN_CENTER);
        namePara.setSpacingAfter(8);
        document.add(namePara);

        // 6. Workshop Achievement Statement
        String bodyText = "has actively participated and successfully completed the technical hands-on workshop on";
        Paragraph bodyPara = new Paragraph(bodyText,
                FontFactory.getFont(FontFactory.HELVETICA, 11, TEXT_MUTED));
        bodyPara.setAlignment(Element.ALIGN_CENTER);
        document.add(bodyPara);

        // 7. Workshop Title
        Paragraph wsTitlePara = new Paragraph("\"" + cert.getWorkshopTitle() + "\"",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 15, BRAND_SECONDARY));
        wsTitlePara.setAlignment(Element.ALIGN_CENTER);
        wsTitlePara.setSpacingBefore(4);
        wsTitlePara.setSpacingAfter(10);
        document.add(wsTitlePara);

        // 8. Date and Venue details
        String detailsText = "Conducted on " + cert.getWorkshopDate() + " at " + cert.getVenue();
        Paragraph detailsPara = new Paragraph(detailsText,
                FontFactory.getFont(FontFactory.HELVETICA, 10, TEXT_DARK));
        detailsPara.setAlignment(Element.ALIGN_CENTER);
        detailsPara.setSpacingAfter(25);
        document.add(detailsPara);

        // 9. Signatures & Verification Table
        PdfPTable footerTable = new PdfPTable(3);
        footerTable.setWidthPercentage(90);
        footerTable.setWidths(new float[]{35f, 30f, 35f});

        // Cell 1: Faculty Coordinator
        PdfPCell cell1 = new PdfPCell();
        cell1.setBorder(Rectangle.NO_BORDER);
        cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
        Paragraph sig1Line = new Paragraph("___________________________", FontFactory.getFont(FontFactory.HELVETICA, 9, TEXT_MUTED));
        sig1Line.setAlignment(Element.ALIGN_CENTER);
        cell1.addElement(sig1Line);
        Paragraph sig1Name = new Paragraph(coordinatorName, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, TEXT_DARK));
        sig1Name.setAlignment(Element.ALIGN_CENTER);
        cell1.addElement(sig1Name);
        Paragraph sig1Title = new Paragraph("Faculty Coordinator, TRIX", FontFactory.getFont(FontFactory.HELVETICA, 8, TEXT_MUTED));
        sig1Title.setAlignment(Element.ALIGN_CENTER);
        cell1.addElement(sig1Title);
        footerTable.addCell(cell1);

        // Cell 2: Certificate ID & Verification Code
        PdfPCell cell2 = new PdfPCell();
        cell2.setBorder(Rectangle.NO_BORDER);
        cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
        Paragraph certNumPara = new Paragraph("Cert ID: " + cert.getCertificateNumber(),
                FontFactory.getFont(FontFactory.COURIER_BOLD, 8, TEXT_DARK));
        certNumPara.setAlignment(Element.ALIGN_CENTER);
        cell2.addElement(certNumPara);

        Paragraph verCodePara = new Paragraph("Verification: " + cert.getVerificationCode(),
                FontFactory.getFont(FontFactory.COURIER, 8, BRAND_PRIMARY));
        verCodePara.setAlignment(Element.ALIGN_CENTER);
        cell2.addElement(verCodePara);

        Paragraph datePara = new Paragraph("Issued: " + cert.getIssueDate(),
                FontFactory.getFont(FontFactory.HELVETICA, 8, TEXT_MUTED));
        datePara.setAlignment(Element.ALIGN_CENTER);
        cell2.addElement(datePara);
        footerTable.addCell(cell2);

        // Cell 3: Head of Department
        PdfPCell cell3 = new PdfPCell();
        cell3.setBorder(Rectangle.NO_BORDER);
        cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
        Paragraph sig2Line = new Paragraph("___________________________", FontFactory.getFont(FontFactory.HELVETICA, 9, TEXT_MUTED));
        sig2Line.setAlignment(Element.ALIGN_CENTER);
        cell3.addElement(sig2Line);
        Paragraph sig2Name = new Paragraph(hodName, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, TEXT_DARK));
        sig2Name.setAlignment(Element.ALIGN_CENTER);
        cell3.addElement(sig2Name);
        Paragraph sig2Title = new Paragraph("Head of Department, CSE", FontFactory.getFont(FontFactory.HELVETICA, 8, TEXT_MUTED));
        sig2Title.setAlignment(Element.ALIGN_CENTER);
        cell3.addElement(sig2Title);
        footerTable.addCell(cell3);

        document.add(footerTable);

        document.close();

        byte[] pdfBytes = baos.toByteArray();

        // Save to server storage folder as well
        saveToStorage(cert.getCertificateNumber(), pdfBytes);

        return pdfBytes;
    }

    private void drawBorders(PdfContentByte cb) {
        float width = PageSize.A4.rotate().getWidth();
        float height = PageSize.A4.rotate().getHeight();

        // Subtle background fill
        cb.saveState();
        cb.setColorFill(LIGHT_BG);
        cb.rectangle(12, 12, width - 24, height - 24);
        cb.fill();

        // Inner white canvas
        cb.setColorFill(Color.WHITE);
        cb.rectangle(20, 20, width - 40, height - 40);
        cb.fill();

        // Primary outer border (thick brand color)
        cb.setColorStroke(BRAND_PRIMARY);
        cb.setLineWidth(3f);
        cb.rectangle(25, 25, width - 50, height - 50);
        cb.stroke();

        // Inner thin border (secondary color)
        cb.setColorStroke(BRAND_SECONDARY);
        cb.setLineWidth(1f);
        cb.rectangle(30, 30, width - 60, height - 60);
        cb.stroke();

        // Elegant corner accents
        cb.setColorStroke(GOLD_ACCENT);
        cb.setLineWidth(2f);
        float cornerLen = 22f;

        // Bottom-left
        cb.moveTo(25, 25 + cornerLen);
        cb.lineTo(25, 25);
        cb.lineTo(25 + cornerLen, 25);
        cb.stroke();

        // Bottom-right
        cb.moveTo(width - 25 - cornerLen, 25);
        cb.lineTo(width - 25, 25);
        cb.lineTo(width - 25, 25 + cornerLen);
        cb.stroke();

        // Top-left
        cb.moveTo(25, height - 25 - cornerLen);
        cb.lineTo(25, height - 25);
        cb.lineTo(25 + cornerLen, height - 25);
        cb.stroke();

        // Top-right
        cb.moveTo(width - 25 - cornerLen, height - 25);
        cb.lineTo(width - 25, height - 25);
        cb.lineTo(width - 25, height - 25 - cornerLen);
        cb.stroke();

        cb.restoreState();
    }

    private void saveToStorage(String certNumber, byte[] bytes) {
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

