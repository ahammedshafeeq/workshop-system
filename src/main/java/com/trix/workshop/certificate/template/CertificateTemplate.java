package com.trix.workshop.certificate.template;

import com.trix.workshop.certificate.model.CertificateData;
import java.awt.Graphics2D;

/**
 * Common contract for all 8 certificate template rendering implementations.
 */
public interface CertificateTemplate {
    String getId();
    String getName();
    String getDescription();
    void paint(Graphics2D g, CertificateData d, int width, int height);
}
