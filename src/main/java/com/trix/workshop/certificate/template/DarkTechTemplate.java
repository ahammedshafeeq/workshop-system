package com.trix.workshop.certificate.template;

import com.trix.workshop.certificate.model.CertificateData;
import com.trix.workshop.certificate.theme.CertificateTheme;
import com.trix.workshop.certificate.util.FontManager;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;

/**
 * Template 3: Dark Tech
 * Futuristic cybernetic aesthetic featuring a deep navy gradient, glowing grid lines,
 * neon cyan and purple accents, vector circuit traces, and Poppins typography.
 */
public class DarkTechTemplate extends BaseCertificateTemplate {

    @Override
    public String getId() {
        return "dark_tech";
    }

    @Override
    public String getName() {
        return "Dark Tech";
    }

    @Override
    public String getDescription() {
        return "High-tech cyber aesthetic with deep navy gradient, neon cyan & purple glow, and circuit traces.";
    }

    @Override
    public void paint(Graphics2D g, CertificateData d, int width, int height) {
        // 1. Deep Midnight Gradient Background (#0B1020 to #111B3A)
        LinearGradientPaint bgGradient = new LinearGradientPaint(
                0, 0, width, height,
                new float[]{0.0f, 1.0f},
                new Color[]{CertificateTheme.TECH_BG_START, CertificateTheme.TECH_BG_END}
        );
        g.setPaint(bgGradient);
        g.fillRect(0, 0, width, height);

        // 2. Subtle Technical Grid Lines
        g.setColor(CertificateTheme.TECH_GRID);
        g.setStroke(new BasicStroke(1.5f));
        int gridSpacing = 90;
        for (int x = 0; x < width; x += gridSpacing) {
            g.drawLine(x, 0, x, height);
        }
        for (int y = 0; y < height; y += gridSpacing) {
            g.drawLine(0, y, width, y);
        }

        // 3. Cybernetic Circuit-Style Corner Lines
        int circuitSize = 220;
        drawCircuitCorner(g, 60, 60, circuitSize, CertificateTheme.TECH_CYAN, CertificateTheme.TECH_PURPLE, false, false);
        drawCircuitCorner(g, width - 60, 60, circuitSize, CertificateTheme.TECH_CYAN, CertificateTheme.TECH_PURPLE, true, false);
        drawCircuitCorner(g, 60, height - 60, circuitSize, CertificateTheme.TECH_CYAN, CertificateTheme.TECH_PURPLE, false, true);
        drawCircuitCorner(g, width - 60, height - 60, circuitSize, CertificateTheme.TECH_CYAN, CertificateTheme.TECH_PURPLE, true, true);

        // 4. Border Frame with Neon Glow
        // Outer Glow Layer
        g.setColor(new Color(CertificateTheme.TECH_CYAN.getRed(), CertificateTheme.TECH_CYAN.getGreen(), CertificateTheme.TECH_CYAN.getBlue(), 35));
        g.setStroke(new BasicStroke(14f));
        g.drawRoundRect(100, 100, width - 200, height - 200, 32, 32);

        // Crisp Inner Line
        g.setColor(CertificateTheme.TECH_CYAN);
        g.setStroke(new BasicStroke(3f));
        g.drawRoundRect(106, 106, width - 212, height - 212, 28, 28);

        // 5. Tech Crest / Logo
        drawLogoPlaceholder(g, width / 2, 170, 105, new Color(0x13, 0x22, 0x47), CertificateTheme.TECH_CYAN);

        // 6. Organization Typography
        Font orgFont = FontManager.getFont(CertificateTheme.FONT_POPPINS, Font.BOLD, 34f);
        drawCenteredString(g, "APJ ABDUL KALAM TECHNOLOGICAL UNIVERSITY", 335, orgFont, CertificateTheme.TECH_CYAN, width);

        Font deptFont = FontManager.getFont(CertificateTheme.FONT_POPPINS, Font.PLAIN, 26f);
        drawCenteredString(g, "DEPARTMENT OF COMPUTER SCIENCE & ENGINEERING • CRYPTOGRAPHIC CREDENTIAL", 378, deptFont, CertificateTheme.TECH_TEXT_MUTED, width);

        // 7. Heading: "CERTIFICATE OF [TYPE]"
        String typeUpper = d.getCertificateType().getDisplayName().toUpperCase();
        Font titleFont = FontManager.getFont(CertificateTheme.FONT_POPPINS, Font.BOLD, CertificateTheme.SIZE_SUPER_TITLE);

        // Layered Neon Glow on Title
        g.setFont(titleFont);
        g.setColor(new Color(CertificateTheme.TECH_PURPLE.getRed(), CertificateTheme.TECH_PURPLE.getGreen(), CertificateTheme.TECH_PURPLE.getBlue(), 60));
        FontMetrics fmTitle = g.getFontMetrics();
        int titleX = (width - fmTitle.stringWidth("CERTIFICATE OF " + typeUpper)) / 2;
        g.drawString("CERTIFICATE OF " + typeUpper, titleX, 545);

        // Crisp White Title Foreground
        g.setColor(CertificateTheme.TECH_TEXT_WHITE);
        g.drawString("CERTIFICATE OF " + typeUpper, titleX, 540);

        // 8. Lead-In Sentence
        Font leadFont = FontManager.getFont(CertificateTheme.FONT_POPPINS, Font.PLAIN, CertificateTheme.SIZE_SUBTITLE);
        drawCenteredString(g, "This digital certificate is officially presented to", 645, leadFont, CertificateTheme.TECH_TEXT_MUTED, width);

        // 9. Auto-Fit Recipient Name (White with Neon Cyan Underline)
        drawAutoFitRecipientName(g, d.getRecipientName(), 810, CertificateTheme.FONT_POPPINS, Font.BOLD, CertificateTheme.TECH_TEXT_WHITE, width);

        // Glowing Neon Cyan Underline Bar
        int underlineW = 760;
        int underlineX = (width - underlineW) / 2;
        g.setColor(new Color(CertificateTheme.TECH_CYAN.getRed(), CertificateTheme.TECH_CYAN.getGreen(), CertificateTheme.TECH_CYAN.getBlue(), 80));
        g.fillRoundRect(underlineX - 4, 860, underlineW + 8, 14, 8, 8);
        g.setColor(CertificateTheme.TECH_CYAN);
        g.fillRoundRect(underlineX, 862, underlineW, 8, 6, 6);

        // 10. Wrapped Achievement Paragraph
        Font bodyFont = FontManager.getFont(CertificateTheme.FONT_POPPINS, Font.PLAIN, CertificateTheme.SIZE_BODY);
        String paragraph = d.getAchievementSentence();
        int bodyY = 970;
        int maxParagraphWidth = (int) (width * 0.72);
        drawWrappedParagraph(g, paragraph, bodyY, maxParagraphWidth, 70, bodyFont, new Color(0xE2, 0xE8, 0xF0), width, true);

        // 11. Central Cyber Security Badge
        int badgeX = (width - 400) / 2;
        int badgeY = height - 570;
        g.setColor(new Color(0x13, 0x1F, 0x3D));
        g.fill(new RoundRectangle2D.Float(badgeX, badgeY, 400, 110, 20, 20));
        g.setColor(CertificateTheme.TECH_PURPLE);
        g.setStroke(new BasicStroke(2.5f));
        g.draw(new RoundRectangle2D.Float(badgeX, badgeY, 400, 110, 20, 20));

        Font cyberFont = FontManager.getFont(CertificateTheme.FONT_POPPINS, Font.BOLD, 22f);
        g.setFont(cyberFont);
        g.setColor(CertificateTheme.TECH_CYAN);
        FontMetrics fmCyber = g.getFontMetrics();
        g.drawString("BLOCKCHAIN & QR AUTHENTICATED", (width - fmCyber.stringWidth("BLOCKCHAIN & QR AUTHENTICATED")) / 2, badgeY + 45);

        Font cyberSub = FontManager.getFont(CertificateTheme.FONT_POPPINS, Font.PLAIN, 18f);
        g.setFont(cyberSub);
        g.setColor(CertificateTheme.TECH_TEXT_MUTED);
        FontMetrics fmSub = g.getFontMetrics();
        g.drawString("VERIFICATION STATUS: 100% VERIFIED", (width - fmSub.stringWidth("VERIFICATION STATUS: 100% VERIFIED")) / 2, badgeY + 80);

        // 12. Dual Signatures in Neon Accents
        int sigY = height - 260;
        drawDualSignatureBlocks(g, d, sigY, width, CertificateTheme.TECH_CYAN, CertificateTheme.TECH_TEXT_MUTED, CertificateTheme.TECH_TEXT_WHITE);

        // 13. Bottom Corner QR Code in Neon Cyan
        int qrSize = CertificateTheme.QR_SIZE;
        int qrX = 170;
        int qrY = height - qrSize - 180;
        drawQRCodeAndMeta(g, d, qrX, qrY, qrSize, CertificateTheme.TECH_CYAN, CertificateTheme.TECH_CYAN);
    }
}
