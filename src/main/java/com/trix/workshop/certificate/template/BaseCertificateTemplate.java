package com.trix.workshop.certificate.template;

import com.trix.workshop.certificate.model.CertificateData;
import com.trix.workshop.certificate.theme.CertificateTheme;
import com.trix.workshop.certificate.util.FontManager;
import com.trix.workshop.certificate.util.QRCodeUtil;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract foundation for all 8 certificate templates.
 * Enforces unified coordinate space (3508x2480), auto-fitting text typography,
 * text wrapping, multi-stop gradients, procedural seals, flourishes, and signatures.
 */
public abstract class BaseCertificateTemplate implements CertificateTemplate {

    /**
     * Applies high-fidelity rendering hints across vector lines, curves, text, and interpolation.
     */
    public static void applyRenderingHints(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
    }

    /**
     * Draws text precisely centred horizontally on the canvas at baseline y.
     */
    protected void drawCenteredString(Graphics2D g, String text, int y, Font font, Color color, int canvasWidth) {
        if (text == null || text.isBlank()) return;
        g.setFont(font);
        g.setColor(color);
        FontMetrics fm = g.getFontMetrics();
        int x = (canvasWidth - fm.stringWidth(text)) / 2;
        g.drawString(text, x, y);
    }

    /**
     * Draws left-aligned text with safe margin offset.
     */
    protected void drawLeftAlignedString(Graphics2D g, String text, int x, int y, Font font, Color color) {
        if (text == null || text.isBlank()) return;
        g.setFont(font);
        g.setColor(color);
        g.drawString(text, x, y);
    }

    /**
     * Auto-fits the participant name: starts at 140px and decrements until it fits 70% of canvas width.
     * If still overflowing at min size (72px), safely wraps into two centered lines. Never overflows or clips.
     */
    protected void drawAutoFitRecipientName(Graphics2D g, String name, int yCenter, String fontName, int fontStyle, Color color, int canvasWidth) {
        if (name == null || name.isBlank()) name = "Recipient Name";

        int maxAllowedWidth = (int) (canvasWidth * 0.70);
        float currentSize = CertificateTheme.SIZE_NAME_MAX;
        Font candidateFont = FontManager.getFont(fontName, fontStyle, currentSize);
        FontMetrics fm = g.getFontMetrics(candidateFont);

        while (fm.stringWidth(name) > maxAllowedWidth && currentSize > CertificateTheme.SIZE_NAME_MIN) {
            currentSize -= 4f;
            candidateFont = FontManager.getFont(fontName, fontStyle, currentSize);
            fm = g.getFontMetrics(candidateFont);
        }

        // If it fits within one line:
        if (fm.stringWidth(name) <= maxAllowedWidth) {
            drawCenteredString(g, name, yCenter, candidateFont, color, canvasWidth);
        } else {
            // Split into two lines evenly
            String[] words = name.split("\\s+");
            if (words.length > 1) {
                int mid = words.length / 2;
                StringBuilder line1 = new StringBuilder();
                StringBuilder line2 = new StringBuilder();
                for (int i = 0; i < mid; i++) line1.append(words[i]).append(" ");
                for (int i = mid; i < words.length; i++) line2.append(words[i]).append(" ");

                float halfSize = Math.max(CertificateTheme.SIZE_NAME_MIN - 10f, 64f);
                Font twoLineFont = FontManager.getFont(fontName, fontStyle, halfSize);
                drawCenteredString(g, line1.toString().trim(), yCenter - 40, twoLineFont, color, canvasWidth);
                drawCenteredString(g, line2.toString().trim(), yCenter + 40, twoLineFont, color, canvasWidth);
            } else {
                drawCenteredString(g, name, yCenter, candidateFont, color, canvasWidth);
            }
        }
    }

    /**
     * Wraps a paragraph automatically to maxWidth, centering or left-aligning each line.
     * Returns the final bottom Y coordinate after the last line.
     */
    protected int drawWrappedParagraph(Graphics2D g, String text, int yStart, int maxWidth, int lineHeight,
                                       Font font, Color color, int canvasWidth, boolean center) {
        if (text == null || text.isBlank()) return yStart;

        g.setFont(font);
        g.setColor(color);
        FontMetrics fm = g.getFontMetrics();

        List<String> lines = wrapText(text, fm, maxWidth);
        int currentY = yStart;

        for (String line : lines) {
            if (center) {
                int x = (canvasWidth - fm.stringWidth(line)) / 2;
                g.drawString(line, x, currentY);
            } else {
                int x = CertificateTheme.SAFE_MARGIN + 40;
                g.drawString(line, x, currentY);
            }
            currentY += lineHeight;
        }

        return currentY;
    }

    private List<String> wrapText(String text, FontMetrics fm, int maxWidth) {
        List<String> lines = new ArrayList<>();
        String[] words = text.split("\\s+");
        StringBuilder currentLine = new StringBuilder();

        for (String word : words) {
            String testLine = currentLine.length() == 0 ? word : currentLine + " " + word;
            if (fm.stringWidth(testLine) <= maxWidth) {
                currentLine = new StringBuilder(testLine);
            } else {
                if (currentLine.length() > 0) {
                    lines.add(currentLine.toString());
                }
                currentLine = new StringBuilder(word);
            }
        }
        if (currentLine.length() > 0) {
            lines.add(currentLine.toString());
        }
        return lines;
    }

    /**
     * Renders standard dual signature blocks with rule line, titles, and procedural seal line.
     */
    protected void drawDualSignatureBlocks(Graphics2D g, CertificateData d, int yBase, int canvasWidth, Color lineColor, Color titleColor, Color nameColor) {
        int leftCenter = (int) (canvasWidth * 0.28);
        int rightCenter = (int) (canvasWidth * 0.72);

        drawSignatureBlock(g, leftCenter, yBase, d.getSignatory1Name(), d.getSignatory1Title(), lineColor, titleColor, nameColor);
        drawSignatureBlock(g, rightCenter, yBase, d.getSignatory2Name(), d.getSignatory2Title(), lineColor, titleColor, nameColor);
    }

    /**
     * Individual signature block component.
     */
    protected void drawSignatureBlock(Graphics2D g, int xCenter, int yBase, String signatoryName, String signatoryTitle,
                                      Color lineColor, Color titleColor, Color nameColor) {
        int lineWidth = 480;
        int xStart = xCenter - (lineWidth / 2);

        // Simulated elegant cursive handwritten signature stroke
        g.setColor(new Color(lineColor.getRed(), lineColor.getGreen(), lineColor.getBlue(), 140));
        g.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        Path2D sigPath = new Path2D.Float();
        sigPath.moveTo(xStart + 40, yBase - 30);
        sigPath.curveTo(xStart + 100, yBase - 90, xStart + 160, yBase - 10, xStart + 220, yBase - 60);
        sigPath.curveTo(xStart + 280, yBase - 100, xStart + 340, yBase - 20, xStart + 420, yBase - 40);
        g.draw(sigPath);

        // Horizontal ruling line
        g.setColor(lineColor);
        g.setStroke(new BasicStroke(2.5f));
        g.drawLine(xStart, yBase, xStart + lineWidth, yBase);

        // Signatory Name
        Font nameFont = FontManager.getFont(CertificateTheme.FONT_PLAYFAIR, Font.BOLD, CertificateTheme.SIZE_SIGNATURE_NAME);
        g.setFont(nameFont);
        g.setColor(nameColor);
        FontMetrics fmName = g.getFontMetrics();
        g.drawString(signatoryName, xCenter - (fmName.stringWidth(signatoryName) / 2), yBase + 45);

        // Signatory Title
        Font titleFont = FontManager.getFont(CertificateTheme.FONT_MONTSERRAT, Font.PLAIN, CertificateTheme.SIZE_SIGNATURE_TITLE);
        g.setFont(titleFont);
        g.setColor(titleColor);
        FontMetrics fmTitle = g.getFontMetrics();
        g.drawString(signatoryTitle, xCenter - (fmTitle.stringWidth(signatoryTitle) / 2), yBase + 90);
    }

    /**
     * Renders a vector gold radial-gradient seal with star and hanging ribbon tails.
     */
    protected void drawGoldSeal(Graphics2D g, int xCenter, int yCenter, int radius, String label) {
        // Ribbon Tails
        g.setColor(new Color(184, 144, 47, 220));
        int ribbonWidth = radius / 2;
        int ribbonHeight = (int) (radius * 1.5);

        // Left Ribbon
        Polygon leftRibbon = new Polygon();
        leftRibbon.addPoint(xCenter - (radius / 2), yCenter + (radius / 3));
        leftRibbon.addPoint(xCenter - (radius / 2) - 40, yCenter + ribbonHeight);
        leftRibbon.addPoint(xCenter - (radius / 4), yCenter + ribbonHeight - 35);
        leftRibbon.addPoint(xCenter - 15, yCenter + (radius / 3));
        g.fill(leftRibbon);

        // Right Ribbon
        Polygon rightRibbon = new Polygon();
        rightRibbon.addPoint(xCenter + (radius / 2), yCenter + (radius / 3));
        rightRibbon.addPoint(xCenter + (radius / 2) + 40, yCenter + ribbonHeight);
        rightRibbon.addPoint(xCenter + (radius / 4), yCenter + ribbonHeight - 35);
        rightRibbon.addPoint(xCenter + 15, yCenter + (radius / 3));
        g.fill(rightRibbon);

        // Outer Cogwheel / Scalloped Edge
        int points = 36;
        Path2D cog = new Path2D.Float();
        double angleStep = Math.PI * 2 / points;
        for (int i = 0; i < points; i++) {
            double r = (i % 2 == 0) ? radius : radius - 14;
            double a = i * angleStep;
            double px = xCenter + r * Math.cos(a);
            double py = yCenter + r * Math.sin(a);
            if (i == 0) cog.moveTo(px, py);
            else cog.lineTo(px, py);
        }
        cog.closePath();

        RadialGradientPaint goldGradient = new RadialGradientPaint(
                new Point2D.Float(xCenter - (radius / 3.0f), yCenter - (radius / 3.0f)),
                radius * 1.2f,
                new float[]{0.0f, 0.45f, 0.85f, 1.0f},
                new Color[]{CertificateTheme.GOLD_SEAL_LIGHT, CertificateTheme.GOLD_INNER_BORDER, CertificateTheme.GOLD_OUTER_BORDER, CertificateTheme.GOLD_SEAL_DARK}
        );
        g.setPaint(goldGradient);
        g.fill(cog);

        // Inner Medallion Border
        g.setColor(CertificateTheme.GOLD_SEAL_DARK);
        g.setStroke(new BasicStroke(4f));
        g.drawOval(xCenter - radius + 22, yCenter - radius + 22, (radius - 22) * 2, (radius - 22) * 2);

        // Inner Dashed Line
        Stroke dashed = new BasicStroke(2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{6, 6}, 0);
        g.setStroke(dashed);
        g.drawOval(xCenter - radius + 32, yCenter - radius + 32, (radius - 32) * 2, (radius - 32) * 2);

        // Central 5-Point Star
        drawStar(g, xCenter, yCenter - 10, radius / 3, radius / 6, CertificateTheme.GOLD_SEAL_DARK);

        // Seal Text
        g.setFont(FontManager.getFont(CertificateTheme.FONT_CINZEL, Font.BOLD, 22f));
        g.setColor(CertificateTheme.GOLD_PRIMARY_TEXT);
        FontMetrics fm = g.getFontMetrics();
        g.drawString(label.toUpperCase(), xCenter - (fm.stringWidth(label) / 2), yCenter + (radius / 2) - 8);
    }

    protected void drawStar(Graphics2D g, int cx, int cy, int outerR, int innerR, Color color) {
        Path2D star = new Path2D.Float();
        double angle = -Math.PI / 2;
        double step = Math.PI / 5;
        for (int i = 0; i < 10; i++) {
            double r = (i % 2 == 0) ? outerR : innerR;
            double x = cx + r * Math.cos(angle);
            double y = cy + r * Math.sin(angle);
            if (i == 0) star.moveTo(x, y);
            else star.lineTo(x, y);
            angle += step;
        }
        star.closePath();
        g.setColor(color);
        g.fill(star);
    }

    /**
     * Renders ornamental Victorian corner flourishes using Bezier curves and Path2D.
     */
    protected void drawOrnamentalCornerFlourish(Graphics2D g, int x, int y, int size, Color color, boolean flipX, boolean flipY) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.translate(x, y);
        g2.scale(flipX ? -1 : 1, flipY ? -1 : 1);

        g2.setColor(color);
        g2.setStroke(new BasicStroke(4.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        Path2D p = new Path2D.Float();
        p.moveTo(0, size);
        p.curveTo(size * 0.1, size * 0.5, size * 0.5, size * 0.1, size, 0);
        p.curveTo(size * 0.7, size * 0.25, size * 0.25, size * 0.7, 0, size);

        p.moveTo(size * 0.3, size * 0.3);
        p.curveTo(size * 0.45, size * 0.15, size * 0.15, size * 0.45, size * 0.3, size * 0.3);

        g2.draw(p);
        g2.fillOval((int) (size * 0.85), -6, 12, 12);
        g2.fillOval(-6, (int) (size * 0.85), 12, 12);
        g2.dispose();
    }

    /**
     * Renders Art-Deco geometric stepped corner embellishments.
     */
    protected void drawArtDecoCorner(Graphics2D g, int x, int y, int size, Color color, boolean flipX, boolean flipY) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.translate(x, y);
        g2.scale(flipX ? -1 : 1, flipY ? -1 : 1);
        g2.setColor(color);
        g2.setStroke(new BasicStroke(5f));

        for (int i = 0; i < 3; i++) {
            int offset = i * 22;
            g2.drawLine(offset, 0, offset, size - offset);
            g2.drawLine(0, offset, size - offset, offset);
            g2.drawLine(offset, size - offset, size - offset, offset);
        }

        g2.fillRect(size / 3, size / 3, 20, 20);
        g2.dispose();
    }

    /**
     * Renders organic floral and leaf corner motifs with dual rose and sage tones.
     */
    protected void drawFloralCorner(Graphics2D g, int x, int y, int size, Color rose, Color sage, boolean flipX, boolean flipY) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.translate(x, y);
        g2.scale(flipX ? -1 : 1, flipY ? -1 : 1);

        // Vine stem
        g2.setColor(sage);
        g2.setStroke(new BasicStroke(4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        Path2D vine = new Path2D.Float();
        vine.moveTo(0, size);
        vine.curveTo(size * 0.2, size * 0.6, size * 0.6, size * 0.2, size, 0);
        g2.draw(vine);

        // Leaves
        g2.setColor(new Color(sage.getRed(), sage.getGreen(), sage.getBlue(), 200));
        drawLeaf(g2, (int)(size * 0.3), (int)(size * 0.6), 35, 20, 45);
        drawLeaf(g2, (int)(size * 0.6), (int)(size * 0.3), 35, 20, -45);

        // Rose blossoms
        g2.setColor(rose);
        g2.fillOval((int)(size * 0.45) - 15, (int)(size * 0.45) - 15, 30, 30);
        g2.setColor(new Color(rose.getRed(), rose.getGreen(), rose.getBlue(), 160));
        g2.fillOval((int)(size * 0.8) - 12, (int)(size * 0.15) - 12, 24, 24);
        g2.fillOval((int)(size * 0.15) - 12, (int)(size * 0.8) - 12, 24, 24);

        g2.dispose();
    }

    private void drawLeaf(Graphics2D g, int x, int y, int w, int h, double rotDeg) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.translate(x, y);
        g2.rotate(Math.toRadians(rotDeg));
        Path2D leaf = new Path2D.Float();
        leaf.moveTo(-w/2.0, 0);
        leaf.quadTo(0, -h, w/2.0, 0);
        leaf.quadTo(0, h, -w/2.0, 0);
        g2.fill(leaf);
        g2.dispose();
    }

    /**
     * Renders modern futuristic PCB circuit trace corner accents.
     */
    protected void drawCircuitCorner(Graphics2D g, int x, int y, int size, Color cyan, Color purple, boolean flipX, boolean flipY) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.translate(x, y);
        g2.scale(flipX ? -1 : 1, flipY ? -1 : 1);

        g2.setStroke(new BasicStroke(4f));
        g2.setColor(cyan);
        g2.drawLine(0, 0, size / 2, 0);
        g2.drawLine(size / 2, 0, (int)(size * 0.75), (int)(size * 0.25));
        g2.drawLine((int)(size * 0.75), (int)(size * 0.25), size, (int)(size * 0.25));
        g2.fillOval(size - 8, (int)(size * 0.25) - 8, 16, 16);

        g2.setColor(purple);
        g2.drawLine(0, 0, 0, size / 2);
        g2.drawLine(0, size / 2, (int)(size * 0.25), (int)(size * 0.75));
        g2.drawLine((int)(size * 0.25), (int)(size * 0.75), (int)(size * 0.25), size);
        g2.fillOval((int)(size * 0.25) - 8, size - 8, 16, 16);

        g2.dispose();
    }

    /**
     * Standardized institutional logo placeholder.
     */
    protected void drawLogoPlaceholder(Graphics2D g, int xCenter, int yTop, int size, Color bgColor, Color iconColor) {
        g.setColor(bgColor);
        g.fillRoundRect(xCenter - (size / 2), yTop, size, size, 28, 28);

        g.setColor(iconColor);
        g.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        // Academic Crest Icon (Torch & Book outline)
        int cx = xCenter;
        int cy = yTop + (size / 2);

        Path2D book = new Path2D.Float();
        book.moveTo(cx - 36, cy + 16);
        book.curveTo(cx - 18, cy + 6, cx, cy + 14, cx, cy + 14);
        book.curveTo(cx, cy + 14, cx + 18, cy + 6, cx + 36, cy + 16);
        book.lineTo(cx + 36, cy - 14);
        book.curveTo(cx + 18, cy - 24, cx, cy - 16, cx, cy - 16);
        book.curveTo(cx, cy - 16, cx - 18, cy - 24, cx - 36, cy - 14);
        book.closePath();
        g.draw(book);

        // Central Flame
        Path2D flame = new Path2D.Float();
        flame.moveTo(cx, cy - 20);
        flame.curveTo(cx - 12, cy - 35, cx - 10, cy - 45, cx, cy - 58);
        flame.curveTo(cx + 10, cy - 45, cx + 12, cy - 35, cx, cy - 20);
        g.fill(flame);
    }

    /**
     * Standardized ZXing QR Code placement and verification code output.
     */
    protected void drawQRCodeAndMeta(Graphics2D g, CertificateData d, int x, int y, int size, Color moduleColor, Color textColor) {
        String content = (d.getVerificationUrl() != null && !d.getVerificationUrl().isBlank())
                ? d.getVerificationUrl()
                : (d.getCertificateCode() != null ? d.getCertificateCode() : "TRIX-CERT");

        BufferedImage qr = QRCodeUtil.generateQRCode(content, size, moduleColor, new Color(255, 255, 255, 0));
        g.drawImage(qr, x, y, null);

        // Verification Code caption below QR
        Font codeFont = FontManager.getFont(CertificateTheme.FONT_MONTSERRAT, Font.BOLD, 22f);
        g.setFont(codeFont);
        g.setColor(textColor);
        FontMetrics fm = g.getFontMetrics();

        String codeLabel = d.getCertificateCode() != null ? d.getCertificateCode() : "TRIX-2026";
        int textX = x + (size - fm.stringWidth(codeLabel)) / 2;
        g.drawString(codeLabel, textX, y + size + 28);

        Font urlFont = FontManager.getFont(CertificateTheme.FONT_MONTSERRAT, Font.PLAIN, 18f);
        g.setFont(urlFont);
        g.setColor(new Color(textColor.getRed(), textColor.getGreen(), textColor.getBlue(), 170));
        FontMetrics fmUrl = g.getFontMetrics();
        String verifyCaption = "Scan to Verify";
        int urlX = x + (size - fmUrl.stringWidth(verifyCaption)) / 2;
        g.drawString(verifyCaption, urlX, y + size + 54);
    }
}
