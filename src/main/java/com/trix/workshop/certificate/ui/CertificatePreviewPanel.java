package com.trix.workshop.certificate.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Interactive Swing JPanel providing real-time, high-fidelity scaled certificate preview.
 * Preserves precise 3508x2480 aspect ratio with antialiased bicubic scaling and drop shadow.
 */
public class CertificatePreviewPanel extends JPanel {

    private BufferedImage fullSizeImage;
    private double zoomFactor = 1.0;
    private boolean fitToWidth = true;

    public CertificatePreviewPanel() {
        setBackground(new Color(0x1E, 0x24, 0x33)); // Deep sleek studio backdrop
        setPreferredSize(new Dimension(860, 610));
    }

    public CertificatePreviewPanel(BufferedImage image) {
        this();
        this.fullSizeImage = image;
    }

    public void setCertificateImage(BufferedImage image) {
        this.fullSizeImage = image;
        repaint();
    }

    public BufferedImage getCertificateImage() {
        return fullSizeImage;
    }

    public void setZoomFactor(double zoom) {
        this.zoomFactor = zoom;
        this.fitToWidth = false;
        revalidate();
        repaint();
    }

    public void setFitToWidth(boolean fit) {
        this.fitToWidth = fit;
        revalidate();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        int panelWidth = getWidth();
        int panelHeight = getHeight();

        if (fullSizeImage == null) {
            // Elegant placeholder card
            g2.setColor(new Color(255, 255, 255, 18));
            int cardW = Math.min(600, panelWidth - 80);
            int cardH = (int) (cardW / 1.414);
            int cardX = (panelWidth - cardW) / 2;
            int cardY = (panelHeight - cardH) / 2;

            g2.fillRoundRect(cardX, cardY, cardW, cardH, 20, 20);
            g2.setColor(new Color(255, 255, 255, 40));
            g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[]{8, 8}, 0));
            g2.drawRoundRect(cardX, cardY, cardW, cardH, 20, 20);

            g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
            g2.setColor(new Color(200, 210, 230));
            String msg = "Select a participant and template to generate live certificate preview";
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(msg, (panelWidth - fm.stringWidth(msg)) / 2, panelHeight / 2);
            g2.dispose();
            return;
        }

        int imgW = fullSizeImage.getWidth();
        int imgH = fullSizeImage.getHeight();
        double aspect = (double) imgW / imgH;

        int drawW;
        int drawH;

        int padding = 40;
        int availableW = Math.max(100, panelWidth - (padding * 2));
        int availableH = Math.max(100, panelHeight - (padding * 2));

        if (fitToWidth) {
            drawW = availableW;
            drawH = (int) Math.round(drawW / aspect);
            if (drawH > availableH) {
                drawH = availableH;
                drawW = (int) Math.round(drawH * aspect);
            }
        } else {
            drawW = (int) Math.round(imgW * zoomFactor * 0.25);
            drawH = (int) Math.round(drawH = (int) (drawW / aspect));
        }

        int drawX = (panelWidth - drawW) / 2;
        int drawY = (panelHeight - drawH) / 2;

        // Layered Realistic Studio Drop Shadow
        for (int i = 8; i >= 1; i--) {
            g2.setColor(new Color(0, 0, 0, 15 - i));
            g2.fillRoundRect(drawX - i, drawY - i + 4, drawW + (i * 2), drawH + (i * 2), 16, 16);
        }

        // Render scaled certificate
        g2.drawImage(fullSizeImage, drawX, drawY, drawW, drawH, null);

        // Thin border stroke around certificate edge
        g2.setColor(new Color(255, 255, 255, 45));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRect(drawX, drawY, drawW, drawH);

        g2.dispose();
    }
}
