package com.trix.workshop.certificate.template;

import com.trix.workshop.certificate.model.CertificateData;
import com.trix.workshop.certificate.theme.CertificateTheme;
import com.trix.workshop.certificate.util.FontManager;

import java.awt.*;

/**
 * Template 5: Corporate Blue
 * Executive enterprise aesthetic with deep navy top header band (#0F3D75), thin gold separation rule,
 * formal serif typography, guilloche-style watermark pattern, and structured dual signatures.
 */
public class CorporateBlueTemplate extends BaseCertificateTemplate {

    @Override
    public String getId() {
        return "corporate_blue";
    }

    @Override
    public String getName() {
        return "Corporate Blue";
    }

    @Override
    public String getDescription() {
        return "Executive enterprise design with navy blue header band, gold accent line, and formal serif typography.";
    }

    @Override
    public void paint(Graphics2D g, CertificateData d, int width, int height) {
        // 1. Crisp Professional Background
        g.setColor(CertificateTheme.CORP_BG);
        g.fillRect(0, 0, width, height);

        // 2. Light Blue Watermark Wave Pattern
        g.setColor(new Color(15, 61, 117, 10));
        g.setStroke(new BasicStroke(2f));
        for (int y = 500; y < height - 300; y += 45) {
            g.drawArc(-100, y, width + 200, 200, 0, 180);
        }

        // 3. Top Executive Header Band (#0F3D75)
        int headerHeight = 340;
        g.setColor(CertificateTheme.CORP_HEADER_BAND);
        g.fillRect(0, 0, width, headerHeight);

        // Thin Gold Separation Line under Header
        g.setColor(CertificateTheme.CORP_GOLD_LINE);
        g.fillRect(0, headerHeight, width, 8);

        // 4. Logo Area and Organisation in White within Header Band
        drawLogoPlaceholder(g, 260, 90, 150, Color.WHITE, CertificateTheme.CORP_HEADER_BAND);

        Font univFont = FontManager.getFont(CertificateTheme.FONT_CINZEL, Font.BOLD, 46f);
        drawLeftAlignedString(g, "APJ ABDUL KALAM TECHNOLOGICAL UNIVERSITY", 390, 160, univFont, Color.WHITE);

        Font deptFont = FontManager.getFont(CertificateTheme.FONT_PLAYFAIR, Font.PLAIN, 32f);
        drawLeftAlignedString(g, "Department of Computer Science and Engineering • Executive Certification", 390, 215, deptFont, new Color(0xBF, 0xDB, 0xFE));

        // 5. Main Heading: "CERTIFICATE OF [TYPE]" in Formal Serif
        String typeUpper = d.getCertificateType().getDisplayName().toUpperCase();
        Font titleFont = FontManager.getFont(CertificateTheme.FONT_CINZEL, Font.BOLD, CertificateTheme.SIZE_SUPER_TITLE);
        drawCenteredString(g, "CERTIFICATE OF " + typeUpper, 560, titleFont, CertificateTheme.CORP_HEADER_BAND, width);

        // Gold Diamond Ornament
        g.setColor(CertificateTheme.CORP_GOLD_LINE);
        g.setStroke(new BasicStroke(3f));
        int ruleW = 480;
        g.drawLine((width - ruleW) / 2, 600, (width + ruleW) / 2, 600);
        Polygon diamond = new Polygon();
        diamond.addPoint(width / 2, 590);
        diamond.addPoint((width / 2) + 12, 600);
        diamond.addPoint(width / 2, 610);
        diamond.addPoint((width / 2) - 12, 600);
        g.fill(diamond);

        // 6. Presentation Lead-in
        Font subFont = FontManager.getFont(CertificateTheme.FONT_PLAYFAIR, Font.ITALIC, CertificateTheme.SIZE_SUBTITLE);
        drawCenteredString(g, "This is formally presented in recognition of the achievements of", 700, subFont, CertificateTheme.CORP_TEXT_NAVY, width);

        // 7. Auto-Fit Recipient Name in Prestigious Serif
        drawAutoFitRecipientName(g, d.getRecipientName(), 880, CertificateTheme.FONT_PLAYFAIR, Font.BOLD, CertificateTheme.CORP_HEADER_BAND, width);

        // 8. Wrapped Achievement Paragraph
        Font bodyFont = FontManager.getFont(CertificateTheme.FONT_PLAYFAIR, Font.PLAIN, CertificateTheme.SIZE_BODY);
        String paragraph = d.getAchievementSentence();
        int bodyY = 1040;
        int maxParagraphWidth = (int) (width * 0.74);
        drawWrappedParagraph(g, paragraph, bodyY, maxParagraphWidth, 70, bodyFont, CertificateTheme.CORP_TEXT_NAVY, width, true);

        // 9. Central Corporate Seal
        int sealRadius = 135;
        int sealY = height - 430;
        drawGoldSeal(g, width / 2, sealY, sealRadius, "OFFICIAL SEAL");

        // 10. Bottom Signature Block with Formal Layout
        int sigY = height - 260;
        drawDualSignatureBlocks(g, d, sigY, width, CertificateTheme.CORP_HEADER_BAND, new Color(0x47, 0x55, 0x69), CertificateTheme.CORP_HEADER_BAND);

        // 11. Bottom Corner ZXing QR Code & Verification Number
        int qrSize = CertificateTheme.QR_SIZE;
        int qrX = 140;
        int qrY = height - qrSize - 160;
        drawQRCodeAndMeta(g, d, qrX, qrY, qrSize, CertificateTheme.CORP_HEADER_BAND, CertificateTheme.CORP_HEADER_BAND);
    }
}
