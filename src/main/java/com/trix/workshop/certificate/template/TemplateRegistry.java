package com.trix.workshop.certificate.template;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Registry mapping template identifiers to their respective CertificateTemplate instances.
 * Adding a 9th template takes just 1 line of code: register(new MyNewTemplate());
 */
public final class TemplateRegistry {

    private static final Map<String, CertificateTemplate> TEMPLATES = new LinkedHashMap<>();

    static {
        register(new ClassicGoldTemplate());
        register(new ModernMinimalTemplate());
        register(new DarkTechTemplate());
        register(new CreativeGradientTemplate());
        register(new CorporateBlueTemplate());
        register(new ElegantFloralTemplate());
        register(new RoyalEmeraldTemplate());
        register(new RetroVintageTemplate());
    }

    private TemplateRegistry() {}

    public static synchronized void register(CertificateTemplate template) {
        TEMPLATES.put(template.getId().toLowerCase(), template);
    }

    public static CertificateTemplate get(String id) {
        if (id == null) return TEMPLATES.get("classic_gold");
        CertificateTemplate t = TEMPLATES.get(id.toLowerCase());
        return t != null ? t : TEMPLATES.get("classic_gold");
    }

    public static CertificateTemplate getTemplateOrDefault(String id) {
        return get(id);
    }

    public static List<CertificateTemplate> getAllTemplates() {
        return Collections.unmodifiableList(new ArrayList<>(TEMPLATES.values()));
    }
}
