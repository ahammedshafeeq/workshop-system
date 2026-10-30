package com.trix.workshop.certificate.engine;

import com.trix.workshop.certificate.model.CertificateData;
import com.trix.workshop.certificate.template.BaseCertificateTemplate;
import com.trix.workshop.certificate.template.CertificateTemplate;
import com.trix.workshop.certificate.theme.CertificateTheme;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * High-performance rendering engine that executes template painting in unified A4 300 DPI coordinate space.
 */
public class CertificateRenderer {

    public static BufferedImage render(CertificateTemplate template, CertificateData data) {
        return new CertificateRenderer().renderToImage(template, data);
    }

    /**
     * Renders a certificate template into a print-ready 3508x2480 300 DPI BufferedImage.
     */
    public BufferedImage renderToImage(CertificateTemplate template, CertificateData data) {
        int width = CertificateTheme.CANVAS_WIDTH;
        int height = CertificateTheme.CANVAS_HEIGHT;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();

        try {
            BaseCertificateTemplate.applyRenderingHints(g2);
            template.paint(g2, data, width, height);
        } finally {
            g2.dispose();
        }

        return image;
    }

    /**
     * Exports the high-resolution certificate image directly to a PNG file.
     */
    public void exportToPng(BufferedImage image, File targetFile) throws IOException {
        if (targetFile.getParentFile() != null && !targetFile.getParentFile().exists()) {
            targetFile.getParentFile().mkdirs();
        }
        ImageIO.write(image, "PNG", targetFile);
    }

    /**
     * Produces a smoothly scaled preview image maintaining aspect ratio for interactive Swing UI.
     */
    public BufferedImage createScaledPreview(BufferedImage fullSizeImage, int previewWidth) {
        int originalWidth = fullSizeImage.getWidth();
        int originalHeight = fullSizeImage.getHeight();
        double scale = (double) previewWidth / originalWidth;
        int previewHeight = (int) Math.round(originalHeight * scale);

        BufferedImage preview = new BufferedImage(previewWidth, previewHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = preview.createGraphics();

        try {
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.drawImage(fullSizeImage, 0, 0, previewWidth, previewHeight, null);
        } finally {
            g2.dispose();
        }

        return preview;
    }
}
