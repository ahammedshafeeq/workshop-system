package com.trix.workshop.certificate.template;

import com.trix.workshop.certificate.model.CertificateData;
import com.trix.workshop.certificate.theme.CertificateTheme;
import com.trix.workshop.certificate.util.FontManager;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Template 2: Modern Minimal
 * Crisp Swiss typography with generous whitespace, a bold 6px cobalt blue edge accent bar,
 * giant light-grey watermark monogram initial, and left-aligned layout exclusively in Montserrat.
 */
public class ModernMinimalTemplate extends BaseCertificateTemplate {

    @Override
    public String getId() {
        return "modern_minimal";
    }

    @Override
    public String getName() {
        return "Modern Minimal";
    }

    @Override
    public String getDescription() {
        return "Contemporary Swiss style featuring stark whitespace, cobalt blue accent bar, and subtle monogram watermark.";
    }

    @Override
    public void paint(Graphics2D g, CertificateData d, int width, int height) {
        // 1. Crisp Pure White Background
        g.setColor(CertificateTheme.MINIMAL_BG);
        g.fillRect(0, 0, width, height);

        // 2. Thin 6px Cobalt Accent Bar on Left Edge
        g.setColor(CertificateTheme.MINIMAL_ACCENT_BAR);
        g.fillRect(0, 0, 16, height); // Substantial 16px solid structural bar on high-res canvas

        int leftX = CertificateTheme.SAFE_MARGIN + 120;

        // 3. Huge Light-Grey Monogram Watermark behind the name area
        String initial = (d.getRecipientName() != null && !d.getRecipientName().isBlank())
                ? d.getRecipientName().substring(0, 1).toUpperCase() : "T";
        Font watermarkFont = FontManager.getFont(CertificateTheme.FONT_MONTSERRAT, Font.BOLD, 750f);
        g.setFont(watermarkFont);
        g.setColor(CertificateTheme.MINIMAL_WATERMARK);
        g.drawString(initial, leftX + 450, 1150);

        // 4. Header & Organization Meta
        Font trackerFont = FontManager.getFont(CertificateTheme.FONT_MONTSERRAT, Font.BOLD, 26f);
        drawLeftAlignedString(g, "ACADEMIC WORKSHOP CREDENTIAL VERIFICATION SYSTEM", leftX, 220, trackerFont, CertificateTheme.MINIMAL_ACCENT_BAR);

        Font orgFont = FontManager.getFont(CertificateTheme.FONT_MONTSERRAT, Font.BOLD, 38f);
        drawLeftAlignedString(g, "APJ ABDUL KALAM TECHNOLOGICAL UNIVERSITY", leftX, 280, orgFont, CertificateTheme.MINIMAL_TEXT_DARK);

        Font deptFont = FontManager.getFont(CertificateTheme.FONT_MONTSERRAT, Font.PLAIN, 28f);
        drawLeftAlignedString(g, "Department of Computer Science & Engineering", leftX, 325, deptFont, CertificateTheme.MINIMAL_TEXT_MUTED);

        // Subtle divider rule
        g.setColor(new Color(226, 232, 240));
        g.setStroke(new BasicStroke(2f));
        g.drawLine(leftX, 375, width - CertificateTheme.SAFE_MARGIN, 375);

        // 5. Title: "CERTIFICATE OF [TYPE]"
        String typeUpper = d.getCertificateType().getDisplayName().toUpperCase();
        Font titleFont = FontManager.getFont(CertificateTheme.FONT_MONTSERRAT, Font.BOLD, 100f);
        drawLeftAlignedString(g, "CERTIFICATE OF " + typeUpper, leftX, 530, titleFont, CertificateTheme.MINIMAL_TEXT_DARK);

        // 6. Presentation Lead-in
        Font subFont = FontManager.getFont(CertificateTheme.FONT_MONTSERRAT, Font.PLAIN, CertificateTheme.SIZE_SUBTITLE);
        drawLeftAlignedString(g, "This official credential is systematically awarded to", leftX, 640, subFont, CertificateTheme.MINIMAL_TEXT_MUTED);

        // 7. Auto-Fit Recipient Name (Left-aligned in Montserrat Bold)
        String name = (d.getRecipientName() != null && !d.getRecipientName().isBlank()) ? d.getRecipientName() : "Recipient Name";
        float nameSize = CertificateTheme.SIZE_NAME_MAX;
        Font nameFont = FontManager.getFont(CertificateTheme.FONT_MONTSERRAT, Font.BOLD, nameSize);
        FontMetrics fm = g.getFontMetrics(nameFont);
        int maxW = (int) (width * 0.75);
        while (fm.stringWidth(name) > maxW && nameSize > CertificateTheme.SIZE_NAME_MIN) {
            nameSize -= 4f;
            nameFont = FontManager.getFont(CertificateTheme.FONT_MONTSERRAT, Font.BOLD, nameSize);
            fm = g.getFontMetrics(nameFont);
        }
        drawLeftAlignedString(g, name, leftX, 810, nameFont, CertificateTheme.MINIMAL_ACCENT_BAR);

        // 8. Body Achievement Paragraph
        Font bodyFont = FontManager.getFont(CertificateTheme.FONT_MONTSERRAT, Font.PLAIN, CertificateTheme.SIZE_BODY);
        String paragraph = d.getAchievementSentence();
        int bodyY = 960;
        int maxParagraphWidth = (int) (width * 0.70);
        drawWrappedParagraph(g, paragraph, bodyY, maxParagraphWidth, 72, bodyFont, CertificateTheme.MINIMAL_TEXT_DARK, width, false);

        // 9. Modern Minimal Badge / Seal on Right Side
        int badgeX = width - CertificateTheme.SAFE_MARGIN - 420;
        int badgeY = 460;
        g.setColor(new Color(238, 242, 255));
        g.fill(new RoundRectangle2D.Float(badgeX, badgeY, 360, 150, 24, 24));
        g.setColor(CertificateTheme.MINIMAL_ACCENT_BAR);
        g.setStroke(new BasicStroke(2.5f));
        g.draw(new RoundRectangle2D.Float(badgeX, badgeY, 360, 150, 24, 24));

        Font badgeTitleFont = FontManager.getFont(CertificateTheme.FONT_MONTSERRAT, Font.BOLD, 24f);
        g.setFont(badgeTitleFont);
        g.drawString("VERIFIED CREDENTIAL", badgeX + 38, badgeY + 54);

        Font badgeStatusFont = FontManager.getFont(CertificateTheme.FONT_MONTSERRAT, Font.PLAIN, 20f);
        g.setColor(CertificateTheme.MINIMAL_TEXT_MUTED);
        g.setFont(badgeStatusFont);
        g.drawString("Status: ISSUED & AUTHENTICATED", badgeX + 38, badgeY + 95);
        g.drawString("Date: " + d.getFormattedIssueDate(), badgeX + 38, badgeY + 125);

        // 10. Left-Aligned Clean Signatures
        int sigY = height - CertificateTheme.SAFE_MARGIN - 220;
        int sig1X = leftX + 200;
        int sig2X = leftX + 900;
        drawSignatureBlock(g, sig1X, sigY, d.getSignatory1Name(), d.getSignatory1Title(),
                CertificateTheme.MINIMAL_ACCENT_BAR, CertificateTheme.MINIMAL_TEXT_MUTED, CertificateTheme.MINIMAL_TEXT_DARK);
        drawSignatureBlock(g, sig2X, sigY, d.getSignatory2Name(), d.getSignatory2Title(),
                CertificateTheme.MINIMAL_ACCENT_BAR, CertificateTheme.MINIMAL_TEXT_MUTED, CertificateTheme.MINIMAL_TEXT_DARK);

        // 11. Bottom Right QR Verification Code
        int qrSize = CertificateTheme.QR_SIZE;
        int qrX = width - CertificateTheme.SAFE_MARGIN - qrSize - 40;
        int qrY = height - CertificateTheme.SAFE_MARGIN - qrSize - 120;
        drawQRCodeAndMeta(g, d, qrX, qrY, qrSize, CertificateTheme.MINIMAL_ACCENT_BAR, CertificateTheme.MINIMAL_TEXT_DARK);
    }
}
