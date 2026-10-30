package com.trix.workshop.certificate.engine;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.PdfWriter;
import com.trix.workshop.certificate.model.CertificateData;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Exporter placing high-resolution A4 landscape certificate graphics into a genuine PDF document via OpenPDF.
 */
public class CertificatePdfExporter {

    /**
     * Converts a 3508x2480 certificate image into an A4 landscape PDF file with zero margins.
     */
    public void exportToPdf(BufferedImage certificateImage, CertificateData data, File outputFile) throws IOException, DocumentException {
        if (outputFile.getParentFile() != null && !outputFile.getParentFile().exists()) {
            outputFile.getParentFile().mkdirs();
        }

        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            exportToPdfStream(certificateImage, data, fos);
        }
    }

    /**
     * Streams the generated PDF directly to an output stream (e.g. HTTP response or file stream).
     */
    public void exportToPdfStream(BufferedImage certificateImage, CertificateData data, OutputStream outputStream) throws IOException, DocumentException {
        // Encode BufferedImage to PNG bytes for lossless insertion into PDF
        ByteArrayOutputStream imageBytes = new ByteArrayOutputStream();
        ImageIO.write(certificateImage, "PNG", imageBytes);
        byte[] rawBytes = imageBytes.toByteArray();

        // Landscape A4 dimensions in OpenPDF points: 842 x 595
        Document document = new Document(PageSize.A4.rotate(), 0, 0, 0, 0);
        PdfWriter writer = PdfWriter.getInstance(document, outputStream);

        // Document Metadata
        document.addTitle("Certificate - " + (data.getRecipientName() != null ? data.getRecipientName() : "Participant"));
        document.addAuthor("APJ Abdul Kalam Technological University - Group TRIX");
        document.addSubject("Academic Certificate of " + data.getCertificateType().getDisplayName());
        document.addCreator("TRIX Workshop Management System");

        document.open();

        Image pdfImage = Image.getInstance(rawBytes);
        pdfImage.scaleAbsolute(PageSize.A4.rotate().getWidth(), PageSize.A4.rotate().getHeight());
        pdfImage.setAbsolutePosition(0, 0);

        document.add(pdfImage);
        document.close();
        writer.close();
    }
}
