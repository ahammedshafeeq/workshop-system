package com.trix.workshop.certificate.template;

import com.trix.workshop.certificate.model.CertificateData;
import com.trix.workshop.certificate.theme.CertificateTheme;
import com.trix.workshop.certificate.util.FontManager;

import java.awt.*;

/**
 * Template 7: Royal Emerald
 * Regal, luxurious aesthetic featuring deep emerald green (#064E3B), radiant gold borders (#FACC15),
 * Art-Deco geometric stepped corner embellishments, centered crest, and Cinzel Roman typography.
 */
public class RoyalEmeraldTemplate extends BaseCertificateTemplate {

    @Override
    public String getId() {
        return "royal_emerald";
    }

    @Override
    public String getName() {
        return "Royal Emerald";
    }

    @Override
    public String getDescription() {
        return "Regal luxury aesthetic with deep forest emerald background, radiant gold frame, and Art-Deco corners.";
    }

    @Override
    public void paint(Graphics2D g, CertificateData d, int width, int height) {
        // 1. Deep Imperial Emerald Canvas (#064E3B)
        g.setColor(CertificateTheme.EMERALD_BG);
        g.fillRect(0, 0, width, height);

        // 2. Radiant Gold Frame
        int margin = 90;
        int frameW = width - (margin * 2);
        int frameH = height - (margin * 2);

        // Heavy Outer Gold Frame (10px)
        g.setColor(CertificateTheme.EMERALD_GOLD);
        g.setStroke(new BasicStroke(10f));
        g.drawRect(margin, margin, frameW, frameH);

        // Thin Inner Gold Frame (3px)
        int innerGap = 28;
        g.setStroke(new BasicStroke(3f));
        g.drawRect(margin + innerGap, margin + innerGap, frameW - (innerGap * 2), frameH - (innerGap * 2));

        // 3. Art-Deco Stepped Geometric Corner Patterns
        int decoSize = 160;
        drawArtDecoCorner(g, margin + innerGap + 5, margin + innerGap + 5, decoSize, CertificateTheme.EMERALD_GOLD, false, false);
        drawArtDecoCorner(g, width - margin - innerGap - 5, margin + innerGap + 5, decoSize, CertificateTheme.EMERALD_GOLD, true, false);
        drawArtDecoCorner(g, margin + innerGap + 5, height - margin - innerGap - 5, decoSize, CertificateTheme.EMERALD_GOLD, false, true);
        drawArtDecoCorner(g, width - margin - innerGap - 5, height - margin - innerGap - 5, decoSize, CertificateTheme.EMERALD_GOLD, true, true);

        // 4. Centered Regal Crest & Institutional Banner
        drawLogoPlaceholder(g, width / 2, margin + 60, 115, CertificateTheme.EMERALD_GOLD, CertificateTheme.EMERALD_BG);

        Font univFont = FontManager.getFont(CertificateTheme.FONT_CINZEL, Font.BOLD, 38f);
        drawCenteredString(g, "APJ ABDUL KALAM TECHNOLOGICAL UNIVERSITY", margin + 225, univFont, CertificateTheme.EMERALD_GOLD, width);

        Font deptFont = FontManager.getFont(CertificateTheme.FONT_CINZEL, Font.PLAIN, 26f);
        drawCenteredString(g, "DEPARTMENT OF COMPUTER SCIENCE & ENGINEERING", margin + 268, deptFont, CertificateTheme.EMERALD_TEXT_GOLD, width);

        // 5. Title: "CERTIFICATE OF [TYPE]" in Cinzel Roman Caps
        String typeUpper = d.getCertificateType().getDisplayName().toUpperCase();
        Font titleFont = FontManager.getFont(CertificateTheme.FONT_CINZEL, Font.BOLD, CertificateTheme.SIZE_SUPER_TITLE);
        drawCenteredString(g, "CERTIFICATE OF " + typeUpper, margin + 410, titleFont, CertificateTheme.EMERALD_GOLD, width);

        // Ornate Triple-Line Divider with Center Diamond
        g.setColor(CertificateTheme.EMERALD_GOLD);
        g.setStroke(new BasicStroke(2.5f));
        int ruleW = 440;
        g.drawLine((width - ruleW) / 2, margin + 440, (width + ruleW) / 2, margin + 440);
        g.fillRect((width / 2) - 8, margin + 432, 16, 16);

        // 6. Presentation Subtitle
        Font subFont = FontManager.getFont(CertificateTheme.FONT_CINZEL, Font.PLAIN, CertificateTheme.SIZE_SUBTITLE);
        drawCenteredString(g, "THIS IS OFFICIALLY CONFERRED UPON", margin + 530, subFont, CertificateTheme.EMERALD_TEXT_WHITE, width);

        // 7. Auto-Fit Recipient Name in Cinzel Bold
        drawAutoFitRecipientName(g, d.getRecipientName(), margin + 710, CertificateTheme.FONT_CINZEL, Font.BOLD, CertificateTheme.EMERALD_GOLD, width);

        // 8. Wrapped Achievement Paragraph
        Font bodyFont = FontManager.getFont(CertificateTheme.FONT_PLAYFAIR, Font.PLAIN, CertificateTheme.SIZE_BODY);
        String paragraph = d.getAchievementSentence();
        int bodyY = margin + 860;
        int maxParagraphWidth = (int) (width * 0.72);
        drawWrappedParagraph(g, paragraph, bodyY, maxParagraphWidth, 70, bodyFont, CertificateTheme.EMERALD_TEXT_WHITE, width, true);

        // 9. Central Royal Gold Seal
        int sealRadius = 145;
        int sealY = height - margin - 350;
        drawGoldSeal(g, width / 2, sealY, sealRadius, "ROYAL ORDER");

        // 10. Dual Signatures in Gold
        int sigY = height - margin - 220;
        drawDualSignatureBlocks(g, d, sigY, width, CertificateTheme.EMERALD_GOLD, CertificateTheme.EMERALD_TEXT_GOLD, CertificateTheme.EMERALD_TEXT_WHITE);

        // 11. Bottom Corner ZXing QR Code in Radiant Gold
        int qrSize = CertificateTheme.QR_SIZE;
        int qrX = margin + innerGap + 60;
        int qrY = height - margin - innerGap - qrSize - 60;
        drawQRCodeAndMeta(g, d, qrX, qrY, qrSize, CertificateTheme.EMERALD_GOLD, CertificateTheme.EMERALD_GOLD);
    }
}
