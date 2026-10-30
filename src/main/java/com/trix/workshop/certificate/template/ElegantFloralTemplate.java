package com.trix.workshop.certificate.template;

import com.trix.workshop.certificate.model.CertificateData;
import com.trix.workshop.certificate.theme.CertificateTheme;
import com.trix.workshop.certificate.util.FontManager;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Template 6: Elegant Floral
 * Gentle refined botanical aesthetic on a soft pastel blush canvas (#FFF5F7),
 * organic rose and sage leaf corner flourishes drawn with Bezier curves,
 * delicate rounded inner frame, and flowing Great Vibes calligraphy.
 */
public class ElegantFloralTemplate extends BaseCertificateTemplate {

    @Override
    public String getId() {
        return "elegant_floral";
    }

    @Override
    public String getName() {
        return "Elegant Floral";
    }

    @Override
    public String getDescription() {
        return "Refined botanical theme with pastel blush background, rose & sage Bezier leaves, and calligraphy.";
    }

    @Override
    public void paint(Graphics2D g, CertificateData d, int width, int height) {
        // 1. Soft Pastel Blush Canvas (#FFF5F7)
        g.setColor(CertificateTheme.FLORAL_BG);
        g.fillRect(0, 0, width, height);

        // 2. Delicate Thin Rounded Inner Frame
        int margin = 90;
        int frameW = width - (margin * 2);
        int frameH = height - (margin * 2);

        g.setColor(new Color(CertificateTheme.FLORAL_ROSE.getRed(), CertificateTheme.FLORAL_ROSE.getGreen(), CertificateTheme.FLORAL_ROSE.getBlue(), 120));
        g.setStroke(new BasicStroke(3.5f));
        g.draw(new RoundRectangle2D.Float(margin, margin, frameW, frameH, 50, 50));

        // Dotted Inner Border
        Stroke dotted = new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[]{4, 8}, 0);
        g.setStroke(dotted);
        g.setColor(CertificateTheme.FLORAL_SAGE);
        g.draw(new RoundRectangle2D.Float(margin + 20, margin + 20, frameW - 40, frameH - 40, 40, 40));

        // 3. Four Floral/Leaf Corner Decorations Drawn with Bezier Curves
        int floralSize = 240;
        drawFloralCorner(g, margin + 25, margin + 25, floralSize, CertificateTheme.FLORAL_ROSE, CertificateTheme.FLORAL_SAGE, false, false);
        drawFloralCorner(g, width - margin - 25, margin + 25, floralSize, CertificateTheme.FLORAL_ROSE, CertificateTheme.FLORAL_SAGE, true, false);
        drawFloralCorner(g, margin + 25, height - margin - 25, floralSize, CertificateTheme.FLORAL_ROSE, CertificateTheme.FLORAL_SAGE, false, true);
        drawFloralCorner(g, width - margin - 25, height - margin - 25, floralSize, CertificateTheme.FLORAL_ROSE, CertificateTheme.FLORAL_SAGE, true, true);

        // 4. Botanical Logo Area
        drawLogoPlaceholder(g, width / 2, margin + 55, 100, CertificateTheme.FLORAL_ROSE, Color.WHITE);

        // 5. University & Department Hierarchy
        Font orgFont = FontManager.getFont(CertificateTheme.FONT_PLAYFAIR, Font.BOLD, 34f);
        drawCenteredString(g, "APJ Abdul Kalam Technological University", margin + 195, orgFont, CertificateTheme.FLORAL_TEXT_DARK, width);

        Font deptFont = FontManager.getFont(CertificateTheme.FONT_MONTSERRAT, Font.PLAIN, 24f);
        drawCenteredString(g, "Department of Computer Science & Engineering", margin + 235, deptFont, new Color(0x7D, 0x5A, 0x68), width);

        // 6. Heading: "CERTIFICATE OF [TYPE]"
        String typeUpper = d.getCertificateType().getDisplayName().toUpperCase();
        Font titleFont = FontManager.getFont(CertificateTheme.FONT_PLAYFAIR, Font.BOLD, CertificateTheme.SIZE_SUPER_TITLE);
        drawCenteredString(g, "CERTIFICATE OF " + typeUpper, margin + 370, titleFont, CertificateTheme.FLORAL_TEXT_DARK, width);

        // Soft Rose Accent Line
        g.setColor(CertificateTheme.FLORAL_ROSE);
        g.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        int ruleW = 380;
        g.drawLine((width - ruleW) / 2, margin + 400, (width + ruleW) / 2, margin + 400);

        // 7. Presentation Subtitle
        Font subFont = FontManager.getFont(CertificateTheme.FONT_PLAYFAIR, Font.ITALIC, CertificateTheme.SIZE_SUBTITLE);
        drawCenteredString(g, "This is proudly and honorably awarded to", margin + 490, subFont, new Color(0x6D, 0x4C, 0x41), width);

        // 8. Auto-Fit Recipient Name in Flowing Great Vibes Calligraphy
        drawAutoFitRecipientName(g, d.getRecipientName(), margin + 670, CertificateTheme.FONT_GREAT_VIBES, Font.PLAIN, CertificateTheme.FLORAL_TEXT_DARK, width);

        // 9. Wrapped Achievement Paragraph
        Font bodyFont = FontManager.getFont(CertificateTheme.FONT_PLAYFAIR, Font.PLAIN, CertificateTheme.SIZE_BODY);
        String paragraph = d.getAchievementSentence();
        int bodyY = margin + 820;
        int maxParagraphWidth = (int) (width * 0.72);
        drawWrappedParagraph(g, paragraph, bodyY, maxParagraphWidth, 68, bodyFont, CertificateTheme.FLORAL_TEXT_DARK, width, true);

        // 10. Rose & Sage Botanical Medallion
        int badgeX = (width - 130) / 2;
        int badgeY = height - margin - 350;
        g.setColor(new Color(255, 255, 255, 200));
        g.fillOval(badgeX, badgeY, 130, 130);
        g.setColor(CertificateTheme.FLORAL_ROSE);
        g.setStroke(new BasicStroke(3f));
        g.drawOval(badgeX, badgeY, 130, 130);
        drawStar(g, width / 2, badgeY + 45, 26, 12, CertificateTheme.FLORAL_ROSE);

        Font badgeFont = FontManager.getFont(CertificateTheme.FONT_PLAYFAIR, Font.BOLD, 18f);
        g.setFont(badgeFont);
        g.setColor(CertificateTheme.FLORAL_TEXT_DARK);
        FontMetrics fmBadge = g.getFontMetrics();
        g.drawString("HONOR", (width - fmBadge.stringWidth("HONOR")) / 2, badgeY + 95);

        // 11. Dual Signature Blocks in Rose & Dark Tone
        int sigY = height - margin - 180;
        drawDualSignatureBlocks(g, d, sigY, width, CertificateTheme.FLORAL_ROSE, new Color(0x7D, 0x5A, 0x68), CertificateTheme.FLORAL_TEXT_DARK);

        // 12. Bottom Left Corner QR Verification Code
        int qrSize = CertificateTheme.QR_SIZE;
        int qrX = margin + 60;
        int qrY = height - margin - qrSize - 60;
        drawQRCodeAndMeta(g, d, qrX, qrY, qrSize, CertificateTheme.FLORAL_ROSE, CertificateTheme.FLORAL_TEXT_DARK);
    }
}
