package com.trix.workshop.certificate.template;

import com.trix.workshop.certificate.model.CertificateData;
import com.trix.workshop.certificate.theme.CertificateTheme;
import com.trix.workshop.certificate.util.FontManager;

import java.awt.*;
import java.util.Random;

/**
 * Template 8: Retro Vintage
 * Historical artisan aesthetic with procedurally generated noise parchment texture,
 * sepia double border, typewriter & antique typography accents, and slightly rotated stamp medallion.
 */
public class RetroVintageTemplate extends BaseCertificateTemplate {

    @Override
    public String getId() {
        return "retro_vintage";
    }

    @Override
    public String getName() {
        return "Retro Vintage";
    }

    @Override
    public String getDescription() {
        return "Artisan antique style featuring parchment noise texture, sepia ruling, and a rotated rubber stamp seal.";
    }

    @Override
    public void paint(Graphics2D g, CertificateData d, int width, int height) {
        // 1. Aged Parchment Base Color
        g.setColor(CertificateTheme.VINTAGE_PARCHMENT);
        g.fillRect(0, 0, width, height);

        // Procedural Grain / Noise Texture
        Random rng = new Random(42L); // Deterministic seed for reproducible parchment grain
        for (int i = 0; i < 45000; i++) {
            int rx = rng.nextInt(width);
            int ry = rng.nextInt(height);
            int shade = rng.nextInt(25);
            g.setColor(new Color(139, 90, 43, shade));
            g.fillRect(rx, ry, 2, 2);
        }

        // 2. Vintage Sepia Double Border
        int margin = 85;
        int borderW = width - (margin * 2);
        int borderH = height - (margin * 2);

        g.setColor(CertificateTheme.VINTAGE_BORDER);
        g.setStroke(new BasicStroke(8f));
        g.drawRect(margin, margin, borderW, borderH);

        // Dashed Typewriter Inner Rule
        Stroke dashed = new BasicStroke(2.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{8, 8}, 0);
        g.setStroke(dashed);
        g.drawRect(margin + 20, margin + 20, borderW - 40, borderH - 40);

        // 3. Vintage Seal / Logo Header
        drawLogoPlaceholder(g, width / 2, margin + 55, 105, CertificateTheme.VINTAGE_BORDER, CertificateTheme.VINTAGE_PARCHMENT);

        Font orgFont = FontManager.getFont(CertificateTheme.FONT_PLAYFAIR, Font.BOLD, 36f);
        drawCenteredString(g, "APJ ABDUL KALAM TECHNOLOGICAL UNIVERSITY", margin + 205, orgFont, CertificateTheme.VINTAGE_SEPIA, width);

        Font deptFont = FontManager.getFont(Font.MONOSPACED, Font.PLAIN, 24f);
        drawCenteredString(g, "[ DEPARTMENT OF COMPUTER SCIENCE & ENGINEERING - ARCHIVAL ARCHIVE ]", margin + 248, deptFont, CertificateTheme.VINTAGE_BORDER, width);

        // 4. Main Heading: "CERTIFICATE OF [TYPE]" in Antique Type
        String typeUpper = d.getCertificateType().getDisplayName().toUpperCase();
        Font titleFont = FontManager.getFont(CertificateTheme.FONT_PLAYFAIR, Font.BOLD, CertificateTheme.SIZE_SUPER_TITLE);
        drawCenteredString(g, "CERTIFICATE OF " + typeUpper, margin + 380, titleFont, CertificateTheme.VINTAGE_SEPIA, width);

        // Sepia ornamental divider rule
        g.setColor(CertificateTheme.VINTAGE_BORDER);
        g.setStroke(new BasicStroke(2.5f));
        int ruleW = 460;
        g.drawLine((width - ruleW) / 2, margin + 410, (width + ruleW) / 2, margin + 410);

        // 5. Presentation Lead-in
        Font subFont = FontManager.getFont(CertificateTheme.FONT_PLAYFAIR, Font.ITALIC, CertificateTheme.SIZE_SUBTITLE);
        drawCenteredString(g, "This is formally certified and affirmed unto", margin + 500, subFont, CertificateTheme.VINTAGE_SEPIA, width);

        // 6. Auto-Fit Recipient Name in Great Vibes Calligraphy
        drawAutoFitRecipientName(g, d.getRecipientName(), margin + 680, CertificateTheme.FONT_GREAT_VIBES, Font.PLAIN, CertificateTheme.VINTAGE_SEPIA, width);

        // 7. Wrapped Achievement Paragraph
        Font bodyFont = FontManager.getFont(CertificateTheme.FONT_PLAYFAIR, Font.PLAIN, CertificateTheme.SIZE_BODY);
        String paragraph = d.getAchievementSentence();
        int bodyY = margin + 830;
        int maxParagraphWidth = (int) (width * 0.72);
        drawWrappedParagraph(g, paragraph, bodyY, maxParagraphWidth, 68, bodyFont, CertificateTheme.VINTAGE_SEPIA, width, true);

        // 8. Rotated Rubber-Stamp Circular Badge
        Graphics2D stampG = (Graphics2D) g.create();
        int stampX = width - margin - 380;
        int stampY = margin + 380;
        stampG.translate(stampX, stampY);
        stampG.rotate(Math.toRadians(-12.0)); // Slightly rotated antique stamp

        stampG.setColor(new Color(CertificateTheme.VINTAGE_STAMP.getRed(), CertificateTheme.VINTAGE_STAMP.getGreen(), CertificateTheme.VINTAGE_STAMP.getBlue(), 210));
        stampG.setStroke(new BasicStroke(4.5f));
        stampG.drawOval(0, 0, 180, 180);
        stampG.drawOval(12, 12, 156, 156);

        Font stampFont = FontManager.getFont(Font.MONOSPACED, Font.BOLD, 19f);
        stampG.setFont(stampFont);
        stampG.drawString("OFFICIAL", 46, 75);
        stampG.drawString("VERIFIED", 46, 105);
        stampG.drawString("ARCHIVE", 50, 135);
        stampG.dispose();

        // 9. Dual Vintage Signatures with Typewriter Titles
        int sigY = height - margin - 220;
        drawDualSignatureBlocks(g, d, sigY, width, CertificateTheme.VINTAGE_BORDER, CertificateTheme.VINTAGE_SEPIA, CertificateTheme.VINTAGE_SEPIA);

        // 10. Bottom Left Corner ZXing QR Code
        int qrSize = CertificateTheme.QR_SIZE;
        int qrX = margin + 60;
        int qrY = height - margin - qrSize - 60;
        drawQRCodeAndMeta(g, d, qrX, qrY, qrSize, CertificateTheme.VINTAGE_BORDER, CertificateTheme.VINTAGE_SEPIA);
    }
}
