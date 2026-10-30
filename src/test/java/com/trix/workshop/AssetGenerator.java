package com.trix.workshop;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.File;

public class AssetGenerator {

    @org.junit.jupiter.api.Test
    public void generateAssets() throws Exception {
        main(new String[0]);
    }

    public static void main(String[] args) throws Exception {
        File imagesDir = new File("src/main/resources/images");
        if (!imagesDir.exists()) {
            imagesDir.mkdirs();
        }

        generateLogo(new File(imagesDir, "logo.png"));
        generateSignature(new File(imagesDir, "signature1.png"), "A. Varma", true);
        generateSignature(new File(imagesDir, "signature2.png"), "L. Priya", false);

        System.out.println("Assets generated successfully in " + imagesDir.getAbsolutePath());
    }

    private static void generateLogo(File file) throws Exception {
        int w = 500;
        int h = 500;
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int cx = w / 2;
        int cy = h / 2;

        // Outer Gold Ring
        g.setColor(new Color(0xD4, 0xAF, 0x37));
        g.setStroke(new BasicStroke(8f));
        g.draw(new Ellipse2D.Double(20, 20, 460, 460));

        // Inner beaded ring
        g.setColor(new Color(0xB8, 0x90, 0x2F));
        g.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10f, new float[]{8f, 12f}, 0f));
        g.draw(new Ellipse2D.Double(35, 35, 430, 430));

        // Central Shield
        Path2D shield = new Path2D.Double();
        shield.moveTo(cx - 130, 110);
        shield.lineTo(cx + 130, 110);
        shield.curveTo(cx + 130, 260, cx + 90, 350, cx, 390);
        shield.curveTo(cx - 90, 350, cx - 130, 260, cx - 130, 110);
        shield.closePath();

        GradientPaint gp = new GradientPaint(cx - 100, 100, new Color(0x0F, 0x3D, 0x75), cx + 100, 380, new Color(0x1E, 0x29, 0x3B));
        g.setPaint(gp);
        g.fill(shield);

        g.setColor(new Color(0xD4, 0xAF, 0x37));
        g.setStroke(new BasicStroke(5f));
        g.draw(shield);

        // Academic Star in Shield
        drawStar(g, cx, 200, 50, 22, new Color(0xFA, 0xCC, 0x15), new Color(0xB8, 0x90, 0x2F));

        // Typography "TRIX" in shield
        g.setFont(new Font(Font.SERIF, Font.BOLD, 46));
        g.setColor(Color.WHITE);
        FontMetrics fm = g.getFontMetrics();
        g.drawString("TRIX", cx - (fm.stringWidth("TRIX") / 2), 300);

        // Subtext "EXCELLENCE"
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        g.setColor(new Color(0xD4, 0xAF, 0x37));
        fm = g.getFontMetrics();
        g.drawString("VERITAS • VIRTUS", cx - (fm.stringWidth("VERITAS • VIRTUS") / 2), 335);

        g.dispose();
        ImageIO.write(img, "png", file);
    }

    private static void generateSignature(File file, String name, boolean variant) throws Exception {
        int w = 450;
        int h = 180;
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        g.setColor(new Color(0x1E, 0x3A, 0x8A, 220)); // Navy ink
        g.setStroke(new BasicStroke(3.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        Path2D p = new Path2D.Double();
        if (variant) {
            p.moveTo(40, 110);
            p.curveTo(80, 20, 110, 30, 90, 120);
            p.curveTo(120, 60, 160, 70, 140, 115);
            p.curveTo(170, 75, 210, 80, 190, 110);
            p.curveTo(220, 85, 260, 90, 240, 112);
            p.curveTo(280, 70, 330, 60, 370, 100);
            // Flourish underline loop
            p.curveTo(390, 120, 300, 155, 120, 145);
            p.curveTo(60, 140, 180, 130, 410, 135);
        } else {
            p.moveTo(50, 90);
            p.curveTo(90, 15, 130, 25, 100, 125);
            p.curveTo(130, 80, 170, 65, 150, 120);
            p.curveTo(180, 70, 230, 80, 210, 115);
            p.curveTo(250, 50, 290, 60, 270, 118);
            p.curveTo(300, 85, 340, 90, 330, 112);
            // Flourish loop
            p.curveTo(350, 130, 280, 160, 140, 150);
            p.curveTo(70, 145, 150, 138, 420, 140);
        }

        g.draw(p);
        g.dispose();
        ImageIO.write(img, "png", file);
    }

    private static void drawStar(Graphics2D g, int cx, int cy, double outerRadius, double innerRadius, Color fill, Color stroke) {
        Path2D star = new Path2D.Double();
        int points = 5;
        for (int i = 0; i < points * 2; i++) {
            double angle = (i * Math.PI / points) - (Math.PI / 2);
            double r = (i % 2 == 0) ? outerRadius : innerRadius;
            double x = cx + r * Math.cos(angle);
            double y = cy + r * Math.sin(angle);
            if (i == 0) star.moveTo(x, y);
            else star.lineTo(x, y);
        }
        star.closePath();

        g.setColor(fill);
        g.fill(star);
        g.setColor(stroke);
        g.setStroke(new BasicStroke(2.5f));
        g.draw(star);
    }
}
