package com.trix.workshop.certificate.util;

import com.trix.workshop.certificate.theme.CertificateTheme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages loading, TTF font registration, and intelligent fallback for certificate typography.
 */
public final class FontManager {
    private static final Logger log = LoggerFactory.getLogger(FontManager.class);
    private static final Map<String, Font> REGISTERED_FONTS = new HashMap<>();
    private static boolean initialized = false;

    private FontManager() {}

    public static synchronized void initialize() {
        if (initialized) return;

        // Fonts to register with their candidate classpath locations
        registerEmbeddedFont(CertificateTheme.FONT_GREAT_VIBES, "/fonts/GreatVibes-Regular.ttf", Font.SERIF);
        registerEmbeddedFont(CertificateTheme.FONT_PLAYFAIR, "/fonts/PlayfairDisplay-Regular.ttf", Font.SERIF);
        registerEmbeddedFont(CertificateTheme.FONT_MONTSERRAT, "/fonts/Montserrat-Regular.ttf", Font.SANS_SERIF);
        registerEmbeddedFont(CertificateTheme.FONT_CINZEL, "/fonts/Cinzel-Regular.ttf", Font.SERIF);
        registerEmbeddedFont(CertificateTheme.FONT_POPPINS, "/fonts/Poppins-Regular.ttf", Font.SANS_SERIF);

        initialized = true;
    }

    private static void registerEmbeddedFont(String fontName, String resourcePath, String fallbackFamily) {
        try (InputStream is = FontManager.class.getResourceAsStream(resourcePath)) {
            if (is != null) {
                Font customFont = Font.createFont(Font.TRUETYPE_FONT, is);
                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                ge.registerFont(customFont);
                REGISTERED_FONTS.put(fontName, customFont);
                log.info("Successfully loaded and registered font [{}] from resource [{}]", fontName, resourcePath);
            } else {
                log.info("Font resource [{}] not found in classpath. Registered fallback [{}] for [{}]", resourcePath, fallbackFamily, fontName);
                REGISTERED_FONTS.put(fontName, new Font(fallbackFamily, Font.PLAIN, 12));
            }
        } catch (Exception e) {
            log.warn("Failed to load embedded font [{}] from [{}]: {}. Using fallback [{}].",
                    fontName, resourcePath, e.getMessage(), fallbackFamily);
            REGISTERED_FONTS.put(fontName, new Font(fallbackFamily, Font.PLAIN, 12));
        }
    }

    /**
     * Retrieves the specified font at the desired style and size, falling back gracefully.
     */
    public static Font getFont(String fontName, int style, float size) {
        if (!initialized) {
            initialize();
        }

        Font base = REGISTERED_FONTS.get(fontName);
        if (base != null) {
            return base.deriveFont(style, size);
        }

        // Secondary fallback to GraphicsEnvironment / standard logical font
        if (CertificateTheme.FONT_GREAT_VIBES.equalsIgnoreCase(fontName)
                || CertificateTheme.FONT_PLAYFAIR.equalsIgnoreCase(fontName)
                || CertificateTheme.FONT_CINZEL.equalsIgnoreCase(fontName)) {
            return new Font(Font.SERIF, style, Math.round(size));
        } else {
            return new Font(Font.SANS_SERIF, style, Math.round(size));
        }
    }
}
