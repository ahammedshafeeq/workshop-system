package com.trix.workshop.certificate.template;

import com.trix.workshop.certificate.model.CertificateData;
import com.trix.workshop.certificate.theme.CertificateTheme;
import com.trix.workshop.certificate.util.FontManager;

import java.awt.*;

/**
 * Template 1: Classic Gold
 * Traditional prestigious collegiate aesthetic with ornamental double gold borders,
 * hand-drawn vector corner flourishes, radial gold seal medallion, and calligraphy.
 */
public class ClassicGoldTemplate extends BaseCertificateTemplate {

    @Override
    public String getId() {
        return "classic_gold";
    }

    @Override
    public String getName() {
        return "Classic Gold";
    }

    @Override
    public String getDescription() {
        return "Traditional collegiate diploma with cream parchment, ornamental double gold borders, and gold star seal.";
    }

    @Override
    public void paint(Graphics2D g, CertificateData d, int width, int height) {
        // 1. Rich Cream Parchment Background
        g.setColor(CertificateTheme.GOLD_BG);
        g.fillRect(0, 0, width, height);

        // 2. Double Ornamental Borders
        int outerMargin = 80;
        int gap = 40;
        int innerMargin = outerMargin + gap;

        // Outer Heavy Border (14px)
        g.setColor(CertificateTheme.GOLD_OUTER_BORDER);
        g.setStroke(new BasicStroke(14f));
        g.drawRect(outerMargin, outerMargin, width - (outerMargin * 2), height - (outerMargin * 2));

        // Inner Refined Border (4px)
        g.setColor(CertificateTheme.GOLD_INNER_BORDER);
        g.setStroke(new BasicStroke(4f));
        g.drawRect(innerMargin, innerMargin, width - (innerMargin * 2), height - (innerMargin * 2));

        // 3. Four Ornamental Corner Flourishes
        int flourishSize = 140;
        drawOrnamentalCornerFlourish(g, innerMargin + 10, innerMargin + 10, flourishSize, CertificateTheme.GOLD_INNER_BORDER, false, false);
        drawOrnamentalCornerFlourish(g, width - innerMargin - 10, innerMargin + 10, flourishSize, CertificateTheme.GOLD_INNER_BORDER, true, false);
        drawOrnamentalCornerFlourish(g, innerMargin + 10, height - innerMargin - 10, flourishSize, CertificateTheme.GOLD_INNER_BORDER, false, true);
        drawOrnamentalCornerFlourish(g, width - innerMargin - 10, height - innerMargin - 10, flourishSize, CertificateTheme.GOLD_INNER_BORDER, true, true);

        // 4. Institutional Logo Placeholder
        drawLogoPlaceholder(g, width / 2, outerMargin + 50, 110, CertificateTheme.GOLD_OUTER_BORDER, Color.WHITE);

        // 5. University & Department Hierarchy
        Font orgFont = FontManager.getFont(CertificateTheme.FONT_CINZEL, Font.BOLD, 36f);
        drawCenteredString(g, "APJ ABDUL KALAM TECHNOLOGICAL UNIVERSITY", outerMargin + 205, orgFont, CertificateTheme.GOLD_PRIMARY_TEXT, width);

        Font deptFont = FontManager.getFont(CertificateTheme.FONT_PLAYFAIR, Font.PLAIN, 28f);
        drawCenteredString(g, "DEPARTMENT OF COMPUTER SCIENCE & ENGINEERING", outerMargin + 248, deptFont, new Color(0x66, 0x55, 0x33), width);

        // 6. Main Certificate Heading: "CERTIFICATE OF [TYPE]"
        String typeUpper = d.getCertificateType().getDisplayName().toUpperCase();
        String titleText = "CERTIFICATE OF " + typeUpper;
        Font titleFont = FontManager.getFont(CertificateTheme.FONT_CINZEL, Font.BOLD, CertificateTheme.SIZE_SUPER_TITLE);
        drawCenteredString(g, titleText, outerMargin + 390, titleFont, CertificateTheme.GOLD_OUTER_BORDER, width);

        // Decorative Accent Rule under title
        g.setColor(CertificateTheme.GOLD_INNER_BORDER);
        g.setStroke(new BasicStroke(3f));
        int ruleW = 420;
        g.drawLine((width - ruleW) / 2, outerMargin + 420, (width + ruleW) / 2, outerMargin + 420);
        g.fillOval((width / 2) - 8, outerMargin + 413, 16, 16);

        // 7. Presentation Subtitle
        Font subFont = FontManager.getFont(CertificateTheme.FONT_PLAYFAIR, Font.ITALIC, CertificateTheme.SIZE_SUBTITLE);
        drawCenteredString(g, "This is proudly presented to", outerMargin + 510, subFont, CertificateTheme.GOLD_PRIMARY_TEXT, width);

        // 8. Auto-Fit Participant Name (Starts at 140px in Great Vibes script)
        drawAutoFitRecipientName(g, d.getRecipientName(), outerMargin + 690, CertificateTheme.FONT_GREAT_VIBES, Font.PLAIN, CertificateTheme.GOLD_PRIMARY_TEXT, width);

        // Underline Flourish under Name
        g.setColor(new Color(CertificateTheme.GOLD_INNER_BORDER.getRed(), CertificateTheme.GOLD_INNER_BORDER.getGreen(), CertificateTheme.GOLD_INNER_BORDER.getBlue(), 120));
        g.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        int nameLineW = 750;
        g.drawLine((width - nameLineW) / 2, outerMargin + 740, (width + nameLineW) / 2, outerMargin + 740);

        // 9. Wrapped Achievement Body Sentence
        Font bodyFont = FontManager.getFont(CertificateTheme.FONT_PLAYFAIR, Font.PLAIN, CertificateTheme.SIZE_BODY);
        String paragraph = d.getAchievementSentence();
        int bodyY = outerMargin + 850;
        int maxParagraphWidth = (int) (width * 0.72);
        drawWrappedParagraph(g, paragraph, bodyY, maxParagraphWidth, 68, bodyFont, CertificateTheme.GOLD_PRIMARY_TEXT, width, true);

        // 10. Central Gold Seal Medallion with Star & Ribbon Tails
        int sealRadius = 145;
        int sealY = height - outerMargin - 320;
        drawGoldSeal(g, width / 2, sealY, sealRadius, d.getCertificateType().getDisplayName());

        // 11. Dual Signature Blocks
        int sigY = height - outerMargin - 260;
        drawDualSignatureBlocks(g, d, sigY, width, CertificateTheme.GOLD_OUTER_BORDER, new Color(0x66, 0x55, 0x33), CertificateTheme.GOLD_PRIMARY_TEXT);

        // 12. Bottom Corner ZXing QR Code & Certificate Code
        int qrSize = CertificateTheme.QR_SIZE;
        int qrX = innerMargin + 50;
        int qrY = height - innerMargin - qrSize - 70;
        drawQRCodeAndMeta(g, d, qrX, qrY, qrSize, CertificateTheme.GOLD_OUTER_BORDER, CertificateTheme.GOLD_PRIMARY_TEXT);
    }
}
