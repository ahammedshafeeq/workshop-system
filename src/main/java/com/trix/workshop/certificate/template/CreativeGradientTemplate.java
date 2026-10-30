package com.trix.workshop.certificate.template;

import com.trix.workshop.certificate.model.CertificateData;
import com.trix.workshop.certificate.theme.CertificateTheme;
import com.trix.workshop.certificate.util.FontManager;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Template 4: Creative Gradient
 * Dynamic modern vibrant theme with a diagonal 3-stop coral-to-yellow-to-green gradient,
 * layered translucent floating geometric shapes, and a central elevated floating white card.
 */
public class CreativeGradientTemplate extends BaseCertificateTemplate {

    @Override
    public String getId() {
        return "creative_gradient";
    }

    @Override
    public String getName() {
        return "Creative Gradient";
    }

    @Override
    public String getDescription() {
        return "Vibrant tri-color diagonal gradient with translucent corner spheres and an elevated white central card.";
    }

    @Override
    public void paint(Graphics2D g, CertificateData d, int width, int height) {
        // 1. Diagonal Tri-Color Gradient (#FF6B6B -> #FFD93D -> #6BCB77)
        LinearGradientPaint triGrad = new LinearGradientPaint(
                0, 0, width, height,
                new float[]{0.0f, 0.5f, 1.0f},
                new Color[]{CertificateTheme.CREATIVE_CORAL, CertificateTheme.CREATIVE_YELLOW, CertificateTheme.CREATIVE_GREEN}
        );
        g.setPaint(triGrad);
        g.fillRect(0, 0, width, height);

        // 2. Translucent Floating Circles & Wave Shapes at Corners
        g.setColor(new Color(255, 255, 255, 45));
        g.fillOval(-150, -150, 600, 600);
        g.fillOval(width - 450, -100, 550, 550);
        g.fillOval(-200, height - 500, 650, 650);
        g.fillOval(width - 400, height - 400, 550, 550);

        g.setColor(new Color(255, 255, 255, 25));
        g.fillOval(width / 4, -200, 800, 800);
        g.fillOval(width / 2, height - 300, 700, 700);

        // 3. Central White Floating Card with Rounded Corners & Soft Drop Shadow
        int cardMarginX = 140;
        int cardMarginY = 120;
        int cardW = width - (cardMarginX * 2);
        int cardH = height - (cardMarginY * 2);
        int cornerArc = 48;

        // Multi-Layered Drop Shadow
        for (int i = 18; i >= 1; i--) {
            g.setColor(new Color(0, 0, 0, 16 - (i / 2)));
            g.fill(new RoundRectangle2D.Float(cardMarginX - i, cardMarginY - i + 10, cardW + (i * 2), cardH + (i * 2), cornerArc + i, cornerArc + i));
        }

        // Crisp White Card Body
        g.setColor(CertificateTheme.CREATIVE_CARD_BG);
        g.fill(new RoundRectangle2D.Float(cardMarginX, cardMarginY, cardW, cardH, cornerArc, cornerArc));

        // 4. Institutional Badge & Header
        drawLogoPlaceholder(g, width / 2, cardMarginY + 50, 100, CertificateTheme.CREATIVE_CORAL, Color.WHITE);

        Font orgFont = FontManager.getFont(CertificateTheme.FONT_POPPINS, Font.BOLD, 32f);
        drawCenteredString(g, "APJ ABDUL KALAM TECHNOLOGICAL UNIVERSITY", cardMarginY + 195, orgFont, CertificateTheme.CREATIVE_TEXT_MAIN, width);

        Font deptFont = FontManager.getFont(CertificateTheme.FONT_POPPINS, Font.PLAIN, 24f);
        drawCenteredString(g, "Department of Computer Science & Engineering • Innovation Initiative", cardMarginY + 235, deptFont, new Color(0x64, 0x74, 0x8B), width);

        // 5. Title: "CERTIFICATE OF [TYPE]" in Bold Poppins with Dual Accent Gradient
        String typeUpper = d.getCertificateType().getDisplayName().toUpperCase();
        Font titleFont = FontManager.getFont(CertificateTheme.FONT_POPPINS, Font.BOLD, CertificateTheme.SIZE_SUPER_TITLE);
        drawCenteredString(g, "CERTIFICATE OF " + typeUpper, cardMarginY + 370, titleFont, CertificateTheme.CREATIVE_CORAL, width);

        // Coral & Green Accent Line
        int barW = 320;
        g.setColor(CertificateTheme.CREATIVE_CORAL);
        g.fillRoundRect((width - barW) / 2, cardMarginY + 400, barW / 2, 8, 4, 4);
        g.setColor(CertificateTheme.CREATIVE_GREEN);
        g.fillRoundRect((width) / 2, cardMarginY + 400, barW / 2, 8, 4, 4);

        // 6. Presentation Lead-in
        Font subFont = FontManager.getFont(CertificateTheme.FONT_POPPINS, Font.PLAIN, CertificateTheme.SIZE_SUBTITLE);
        drawCenteredString(g, "This certificate is enthusiastically presented to", cardMarginY + 485, subFont, new Color(0x47, 0x55, 0x69), width);

        // 7. Auto-Fit Recipient Name in Bold Poppins
        drawAutoFitRecipientName(g, d.getRecipientName(), cardMarginY + 650, CertificateTheme.FONT_POPPINS, Font.BOLD, CertificateTheme.CREATIVE_TEXT_MAIN, width);

        // 8. Wrapped Achievement Paragraph
        Font bodyFont = FontManager.getFont(CertificateTheme.FONT_POPPINS, Font.PLAIN, CertificateTheme.SIZE_BODY);
        String paragraph = d.getAchievementSentence();
        int bodyY = cardMarginY + 800;
        int maxParagraphWidth = (int) (cardW * 0.78);
        drawWrappedParagraph(g, paragraph, bodyY, maxParagraphWidth, 68, bodyFont, new Color(0x33, 0x41, 0x55), width, true);

        // 9. Central Round Creative Badge
        int badgeX = (width - 130) / 2;
        int badgeY = cardMarginY + cardH - 330;
        g.setColor(new Color(0xFF, 0xF7, 0xED));
        g.fillOval(badgeX, badgeY, 130, 130);
        g.setColor(CertificateTheme.CREATIVE_CORAL);
        g.setStroke(new BasicStroke(3.5f));
        g.drawOval(badgeX, badgeY, 130, 130);
        drawStar(g, width / 2, badgeY + 45, 26, 12, CertificateTheme.CREATIVE_CORAL);

        Font badgeFont = FontManager.getFont(CertificateTheme.FONT_POPPINS, Font.BOLD, 17f);
        g.setFont(badgeFont);
        g.setColor(CertificateTheme.CREATIVE_TEXT_MAIN);
        FontMetrics fmBadge = g.getFontMetrics();
        g.drawString("EXCELLENCE", (width - fmBadge.stringWidth("EXCELLENCE")) / 2, badgeY + 95);

        // 10. Dual Signatures
        int sigY = cardMarginY + cardH - 180;
        drawDualSignatureBlocks(g, d, sigY, width, CertificateTheme.CREATIVE_CORAL, new Color(0x64, 0x74, 0x8B), CertificateTheme.CREATIVE_TEXT_MAIN);

        // 11. Bottom Corner ZXing QR Code
        int qrSize = CertificateTheme.QR_SIZE;
        int qrX = cardMarginX + 50;
        int qrY = cardMarginY + cardH - qrSize - 50;
        drawQRCodeAndMeta(g, d, qrX, qrY, qrSize, CertificateTheme.CREATIVE_CORAL, CertificateTheme.CREATIVE_TEXT_MAIN);
    }
}
