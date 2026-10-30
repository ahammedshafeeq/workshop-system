package com.trix.workshop.certificate.theme;

import java.awt.Color;
import java.awt.Font;

/**
 * Certificate Design System Constants & Theme Definition
 * Strict elimination of magic numbers across all 8 certificate templates.
 */
public final class CertificateTheme {

    private CertificateTheme() {}

    // --- Canvas Dimensions (A4 Landscape at 300 DPI) ---
    public static final int CANVAS_WIDTH = 3508;
    public static final int CANVAS_HEIGHT = 2480;
    public static final int DPI = 300;
    public static final int SAFE_MARGIN = 150;

    // --- Font Family Designations ---
    public static final String FONT_GREAT_VIBES = "Great Vibes";
    public static final String FONT_PLAYFAIR = "Playfair Display";
    public static final String FONT_MONTSERRAT = "Montserrat";
    public static final String FONT_CINZEL = "Cinzel";
    public static final String FONT_POPPINS = "Poppins";

    // --- Standard Font Sizes (in pixels at 3508x2480) ---
    public static final float SIZE_SUPER_TITLE = 110f;
    public static final float SIZE_TITLE = 92f;
    public static final float SIZE_SUBTITLE = 50f;
    public static final float SIZE_NAME_MAX = 140f;
    public static final float SIZE_NAME_MIN = 72f;
    public static final float SIZE_BODY = 46f;
    public static final float SIZE_BODY_EMPHASIS = 52f;
    public static final float SIZE_META = 38f;
    public static final float SIZE_CODE = 30f;
    public static final float SIZE_SIGNATURE_TITLE = 34f;
    public static final float SIZE_SIGNATURE_NAME = 42f;

    // --- Color Palette: Template 1 - Classic Gold ---
    public static final Color GOLD_BG = new Color(0xFB, 0xF6, 0xE9);
    public static final Color GOLD_OUTER_BORDER = new Color(0xB8, 0x90, 0x2F);
    public static final Color GOLD_INNER_BORDER = new Color(0xD4, 0xAF, 0x37);
    public static final Color GOLD_PRIMARY_TEXT = new Color(0x2B, 0x24, 0x16);
    public static final Color GOLD_ACCENT = new Color(0xD4, 0xAF, 0x37);
    public static final Color GOLD_SEAL_DARK = new Color(0x99, 0x73, 0x1A);
    public static final Color GOLD_SEAL_LIGHT = new Color(0xF3, 0xE5, 0xAB);

    // --- Color Palette: Template 2 - Modern Minimal ---
    public static final Color MINIMAL_BG = new Color(0xFF, 0xFF, 0xFF);
    public static final Color MINIMAL_ACCENT_BAR = new Color(0x25, 0x63, 0xEB);
    public static final Color MINIMAL_TEXT_DARK = new Color(0x0F, 0x17, 0x2A);
    public static final Color MINIMAL_TEXT_MUTED = new Color(0x64, 0x74, 0x8B);
    public static final Color MINIMAL_WATERMARK = new Color(0xF1, 0xF5, 0xF9);

    // --- Color Palette: Template 3 - Dark Tech ---
    public static final Color TECH_BG_START = new Color(0x0B, 0x10, 0x20);
    public static final Color TECH_BG_END = new Color(0x11, 0x1B, 0x3A);
    public static final Color TECH_CYAN = new Color(0x22, 0xD3, 0xEE);
    public static final Color TECH_PURPLE = new Color(0xA8, 0x55, 0xF7);
    public static final Color TECH_GRID = new Color(0x1E, 0x29, 0x4B, 0x55);
    public static final Color TECH_TEXT_WHITE = new Color(0xFF, 0xFF, 0xFF);
    public static final Color TECH_TEXT_MUTED = new Color(0x94, 0xA3, 0xB8);

    // --- Color Palette: Template 4 - Creative Gradient ---
    public static final Color CREATIVE_CORAL = new Color(0xFF, 0x6B, 0x6B);
    public static final Color CREATIVE_YELLOW = new Color(0xFF, 0xD9, 0x3D);
    public static final Color CREATIVE_GREEN = new Color(0x6B, 0xCB, 0x77);
    public static final Color CREATIVE_CARD_BG = new Color(0xFF, 0xFF, 0xFF);
    public static final Color CREATIVE_TEXT_MAIN = new Color(0x1F, 0x29, 0x37);

    // --- Color Palette: Template 5 - Corporate Blue ---
    public static final Color CORP_HEADER_BAND = new Color(0x0F, 0x3D, 0x75);
    public static final Color CORP_GOLD_LINE = new Color(0xD4, 0xAF, 0x37);
    public static final Color CORP_BG = new Color(0xFA, 0xFC, 0xFF);
    public static final Color CORP_TEXT_NAVY = new Color(0x0A, 0x25, 0x40);

    // --- Color Palette: Template 6 - Elegant Floral ---
    public static final Color FLORAL_BG = new Color(0xFF, 0xF5, 0xF7);
    public static final Color FLORAL_ROSE = new Color(0xE8, 0xA0, 0xB4);
    public static final Color FLORAL_SAGE = new Color(0xA8, 0xC3, 0xA0);
    public static final Color FLORAL_TEXT_DARK = new Color(0x3E, 0x27, 0x23);

    // --- Color Palette: Template 7 - Royal Emerald ---
    public static final Color EMERALD_BG = new Color(0x06, 0x4E, 0x3B);
    public static final Color EMERALD_GOLD = new Color(0xFA, 0xCC, 0x15);
    public static final Color EMERALD_TEXT_GOLD = new Color(0xFE, 0xF0, 0x8A);
    public static final Color EMERALD_TEXT_WHITE = new Color(0xF0, 0xFD, 0xF4);

    // --- Color Palette: Template 8 - Retro Vintage ---
    public static final Color VINTAGE_PARCHMENT = new Color(0xF4, 0xEC, 0xD8);
    public static final Color VINTAGE_SEPIA = new Color(0x5C, 0x40, 0x33);
    public static final Color VINTAGE_BORDER = new Color(0x8B, 0x5A, 0x2B);
    public static final Color VINTAGE_STAMP = new Color(0xA7, 0x37, 0x37);

    // --- QR Code & Seal Dimensions ---
    public static final int QR_SIZE = 260;
    public static final int SEAL_SIZE = 300;
}
