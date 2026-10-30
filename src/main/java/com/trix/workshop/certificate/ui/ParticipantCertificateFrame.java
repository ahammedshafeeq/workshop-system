package com.trix.workshop.certificate.ui;

import com.trix.workshop.certificate.dao.CertificateDAO;
import com.trix.workshop.certificate.model.CertificateData;
import com.trix.workshop.certificate.service.PremiumCertificateService;
import com.trix.workshop.certificate.template.CertificateTemplate;
import com.trix.workshop.certificate.template.TemplateRegistry;

import javax.sql.DataSource;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Modern Swing Participant UI displaying enrolled workshops and earned certificates.
 * Provides scaled preview, PDF/PNG download, and enforces attendance locks.
 */
public class ParticipantCertificateFrame extends JFrame {

    private final DataSource dataSource;
    private final PremiumCertificateService certificateService;
    private final long currentParticipantId;

    private JTable certificateTable;
    private DefaultTableModel tableModel;
    private CertificatePreviewPanel previewPanel;
    private JButton btnPreview;
    private JButton btnDownloadPdf;
    private JButton btnDownloadPng;
    private JLabel statusLabel;

    private BufferedImage currentImage;
    private CertificateData currentData;

    public ParticipantCertificateFrame(DataSource dataSource, long participantId) {
        this.dataSource = dataSource;
        this.certificateService = new PremiumCertificateService(dataSource);
        this.currentParticipantId = participantId;

        setTitle("TRIX Workshops • My Verified Academic Certificates");
        setSize(1300, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initUI();
        loadCertificates();
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
        root.setBackground(new Color(0xF8, 0xFA, 0xFC));

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setResizeWeight(0.40);
        split.setContinuousLayout(true);

        // Left Table
        JPanel left = new JPanel(new BorderLayout(8, 8));
        left.setBackground(Color.WHITE);
        left.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xE2, 0xE8, 0xF0)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JLabel title = new JLabel("Enrolled Sessions & Certificate Status");
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
        left.add(title, BorderLayout.NORTH);

        String[] cols = {"Workshop Title", "Date", "Attendance", "Certificate Status", "Code", "Template"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        certificateTable = new JTable(tableModel);
        certificateTable.setRowHeight(28);
        certificateTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) updateButtonStates();
        });

        left.add(new JScrollPane(certificateTable), BorderLayout.CENTER);

        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        btnBar.setBackground(Color.WHITE);

        btnPreview = new JButton("Preview Certificate");
        btnPreview.setBackground(new Color(0x0D, 0x94, 0x88));
        btnPreview.setForeground(Color.WHITE);
        btnPreview.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        btnPreview.setEnabled(false);
        btnPreview.addActionListener(e -> onPreviewClicked());

        btnDownloadPdf = new JButton("Download PDF");
        btnDownloadPdf.setEnabled(false);
        btnDownloadPdf.addActionListener(e -> downloadFile("pdf"));

        btnDownloadPng = new JButton("Download PNG");
        btnDownloadPng.setEnabled(false);
        btnDownloadPng.addActionListener(e -> downloadFile("png"));

        btnBar.add(btnPreview);
        btnBar.add(btnDownloadPdf);
        btnBar.add(btnDownloadPng);
        left.add(btnBar, BorderLayout.SOUTH);

        split.setLeftComponent(left);

        // Right Preview
        JPanel right = new JPanel(new BorderLayout(8, 8));
        right.setBackground(Color.WHITE);
        right.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xE2, 0xE8, 0xF0)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JLabel pTitle = new JLabel("Certificate Preview");
        pTitle.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
        right.add(pTitle, BorderLayout.NORTH);

        previewPanel = new CertificatePreviewPanel();
        right.add(previewPanel, BorderLayout.CENTER);

        split.setRightComponent(right);
        root.add(split, BorderLayout.CENTER);

        statusLabel = new JLabel("Select an issued certificate to preview or download.");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        root.add(statusLabel, BorderLayout.SOUTH);

        setContentPane(root);
    }

    private void loadCertificates() {
        tableModel.setRowCount(0);
        String sql = "SELECT w.title, w.workshop_date, " +
                     "COALESCE(a.attendance_status, 'NOT_RECORDED') as attendance_status, " +
                     "c.certificate_status, " +
                     "COALESCE(c.certificate_code, c.certificate_number) as code, " +
                     "COALESCE(c.template_id, w.certificate_template, 'classic_gold') as template_id, " +
                     "u.full_name, w.venue, w.organizer_name, c.certificate_type, c.issue_date " +
                     "FROM registrations r " +
                     "JOIN workshops w ON r.workshop_id = w.workshop_id " +
                     "JOIN users u ON r.participant_id = u.user_id " +
                     "LEFT JOIN attendance a ON r.registration_id = a.registration_id " +
                     "LEFT JOIN certificates c ON r.registration_id = c.registration_id " +
                     "WHERE r.participant_id = ? AND r.registration_status = 'APPROVED' " +
                     "ORDER BY w.workshop_date DESC";

        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, currentParticipantId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String status = rs.getString("certificate_status");
                    tableModel.addRow(new Object[]{
                            rs.getString("title"),
                            rs.getString("workshop_date"),
                            rs.getString("attendance_status"),
                            status != null ? status : "Not Issued",
                            rs.getString("code") != null ? rs.getString("code") : "-",
                            rs.getString("template_id"),
                            rs.getString("full_name"),
                            rs.getString("venue"),
                            rs.getString("organizer_name"),
                            rs.getString("certificate_type")
                    });
                }
            }
        } catch (Exception e) {
            statusLabel.setText("Error loading certificates: " + e.getMessage());
        }
    }

    private void updateButtonStates() {
        int row = certificateTable.getSelectedRow();
        if (row < 0) {
            btnPreview.setEnabled(false);
            btnDownloadPdf.setEnabled(false);
            btnDownloadPng.setEnabled(false);
            return;
        }

        String att = (String) tableModel.getValueAt(row, 2);
        String certStatus = (String) tableModel.getValueAt(row, 3);
        boolean isIssued = "ISSUED".equalsIgnoreCase(certStatus) || "GENERATED".equalsIgnoreCase(certStatus);
        boolean isPresent = "PRESENT".equalsIgnoreCase(att);

        if (!isPresent) {
            btnPreview.setEnabled(false);
            btnDownloadPdf.setEnabled(false);
            btnDownloadPng.setEnabled(false);
            statusLabel.setText("Locked: Attendance status is '" + att + "'. Certificate unavailable.");
        } else if (!isIssued) {
            btnPreview.setEnabled(false);
            btnDownloadPdf.setEnabled(false);
            btnDownloadPng.setEnabled(false);
            statusLabel.setText("Pending: Attendance verified (PRESENT). Awaiting faculty administrator release.");
        } else {
            btnPreview.setEnabled(true);
            statusLabel.setText("Ready for preview and download.");
        }
    }

    private void onPreviewClicked() {
        int row = certificateTable.getSelectedRow();
        if (row < 0) return;

        String wsTitle = (String) tableModel.getValueAt(row, 0);
        String wsDate = (String) tableModel.getValueAt(row, 1);
        String code = (String) tableModel.getValueAt(row, 4);
        String templateId = (String) tableModel.getValueAt(row, 5);
        String name = (String) tableModel.getValueAt(row, 6);
        String venue = (String) tableModel.getValueAt(row, 7);
        String org = (String) tableModel.getValueAt(row, 8);
        String certType = (String) tableModel.getValueAt(row, 9);

        CertificateTemplate template = TemplateRegistry.get(templateId);

        currentData = new CertificateData(
                name,
                wsTitle,
                wsDate,
                venue != null ? venue : "Main Auditorium",
                org != null ? org : "Department Faculty",
                code,
                CertificateData.CertificateType.fromString(certType),
                java.time.LocalDate.now()
        );
        currentData.setVerificationUrl("http://localhost:8080/verify.html?code=" + code);

        currentImage = certificateService.getRenderer().renderToImage(template, currentData);
        previewPanel.setCertificateImage(currentImage);

        btnDownloadPdf.setEnabled(true);
        btnDownloadPng.setEnabled(true);
        statusLabel.setText("Certificate loaded (" + template.getName() + ").");
    }

    private void downloadFile(String format) {
        if (currentImage == null || currentData == null) return;

        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("my_certificate_" + currentData.getCertificateCode().replace("-", "_") + "." + format));

        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File target = chooser.getSelectedFile();
            try {
                if ("pdf".equalsIgnoreCase(format)) {
                    certificateService.getPdfExporter().exportToPdf(currentImage, currentData, target);
                } else {
                    certificateService.getRenderer().exportToPng(currentImage, target);
                }

                int choice = JOptionPane.showOptionDialog(this,
                        "Your certificate was successfully saved to:\n" + target.getAbsolutePath(),
                        "Download Complete",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.INFORMATION_MESSAGE,
                        null,
                        new Object[]{"Open File", "Done"},
                        "Open File");

                if (choice == 0 && Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(target);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Save failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
