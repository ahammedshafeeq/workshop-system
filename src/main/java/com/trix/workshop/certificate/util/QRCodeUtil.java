package com.trix.workshop.certificate.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 * High-resolution QR code generator utilizing ZXing with custom module coloring and transparency.
 */
public final class QRCodeUtil {

    private QRCodeUtil() {}

    /**
     * Generates a QR code image with specified dimensions, module color, and background color.
     * Supports transparent background (Color with alpha = 0).
     */
    public static BufferedImage generateQRCode(String content, int size, Color moduleColor, Color backgroundColor) {
        if (content == null || content.isBlank()) {
            content = "INVALID_CODE";
        }

        try {
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            hints.put(EncodeHintType.MARGIN, 1);
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, size, size, hints);

            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();

            boolean isTransparent = backgroundColor == null || backgroundColor.getAlpha() == 0;
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = image.createGraphics();

            if (!isTransparent) {
                g2.setColor(backgroundColor);
                g2.fillRect(0, 0, width, height);
            }

            int moduleRgb = moduleColor != null ? moduleColor.getRGB() : Color.BLACK.getRGB();

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    if (bitMatrix.get(x, y)) {
                        image.setRGB(x, y, moduleRgb);
                    } else if (!isTransparent) {
                        image.setRGB(x, y, backgroundColor.getRGB());
                    }
                }
            }

            g2.dispose();
            return image;
        } catch (Exception e) {
            // Safe fallback image with border and error text
            BufferedImage fallback = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = fallback.createGraphics();
            g.setColor(new Color(240, 240, 240));
            g.fillRect(0, 0, size, size);
            g.setColor(Color.DARK_GRAY);
            g.drawRect(2, 2, size - 4, size - 4);
            g.drawString("QR Error", size / 4, size / 2);
            g.dispose();
            return fallback;
        }
    }
}
