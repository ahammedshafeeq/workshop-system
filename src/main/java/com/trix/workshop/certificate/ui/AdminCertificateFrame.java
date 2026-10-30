package com.trix.workshop.certificate.ui;

import com.trix.workshop.certificate.dao.CertificateDAO;
import com.trix.workshop.certificate.engine.CertificatePdfExporter;
import com.trix.workshop.certificate.engine.CertificateRenderer;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Modern Swing Admin Management UI for workshop certificates:
 * Real-time preview, template selection, background generation with SwingWorker,
 * PDF/PNG export, and default workshop template designation.
 */
public class AdminCertificateFrame extends JFrame {

    private final DataSource dataSource;
    private final PremiumCertificateService certificateService;
    private final CertificateDAO certificateDAO;

    private JComboBox<WorkshopItem> workshopCombo;
    private JComboBox<TemplateItem> templateCombo;
    private JComboBox<CertificateData.CertificateType> typeCombo;
    private JTable participantTable;
    private DefaultTableModel tableModel;
    private CertificatePreviewPanel previewPanel;
    private JProgressBar progressBar;
    private JLabel statusLabel;
    private JButton btnPreview;
    private JButton btnGenerateSelected;
    private JButton btnGenerateAll;
    private JButton btnSetDefault;
    private JButton btnExportPdf;
    private JButton btnExportPng;

    private BufferedImage currentPreviewImage;
    private CertificateData currentPreviewData;

    public static class WorkshopItem {
        public final long id;
        public final String title;
        public WorkshopItem(long id, String title) { this.id = id; this.title = title; }
        @Override public String toString() { return id + " - " + title; }
    }

    public static class TemplateItem {
        public final CertificateTemplate template;
        public TemplateItem(CertificateTemplate template) { this.template = template; }
        @Override public String toString() { return template.getName(); }
    }

    public AdminCertificateFrame(DataSource dataSource) {
        this.dataSource = dataSource;
        this.certificateService = new PremiumCertificateService(dataSource);
        this.certificateDAO = new CertificateDAO();

        setTitle("TRIX Workshops • Administrator Certificate Studio & Generation");
        setSize(1380, 860);
        setMinimumSize(new Dimension(1100, 700));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initUI();
        loadWorkshops();
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
        root.setBackground(new Color(0xF1, 0xF5, 0xF9));

        // --- Top Control Toolbar ---
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 8));
        topBar.setBackground(Color.WHITE);
        topBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xE2, 0xE8, 0xF0), 1),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));

        topBar.add(new JLabel("Workshop:"));
        workshopCombo = new JComboBox<>();
        workshopCombo.setPreferredSize(new Dimension(320, 32));
        workshopCombo.addActionListener(e -> onWorkshopSelected());
        topBar.add(workshopCombo);

        topBar.add(new JLabel("Template:"));
        templateCombo = new JComboBox<>();
        for (CertificateTemplate t : TemplateRegistry.getAllTemplates()) {
            templateCombo.addItem(new TemplateItem(t));
        }
        templateCombo.setPreferredSize(new Dimension(170, 32));
        topBar.add(templateCombo);

        btnSetDefault = new JButton("Set As Workshop Default");
        btnSetDefault.addActionListener(e -> setAsWorkshopDefault());
        topBar.add(btnSetDefault);

        topBar.add(new JLabel("Type Override:"));
        typeCombo = new JComboBox<>(CertificateData.CertificateType.values());
        typeCombo.setPreferredSize(new Dimension(140, 32));
        topBar.add(typeCombo);

        root.add(topBar, BorderLayout.NORTH);

        // --- Center Split: Participants Table on Left, Live Preview on Right ---
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.42);
        splitPane.setContinuousLayout(true);

        // Left Panel: Participants
        JPanel leftPanel = new JPanel(new BorderLayout(8, 8));
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xE2, 0xE8, 0xF0), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JLabel leftTitle = new JLabel("Enrolled Participants & Eligibility");
        leftTitle.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
        leftPanel.add(leftTitle, BorderLayout.NORTH);

        String[] cols = {"Reg ID", "Participant Name", "Email", "Attendance", "Certificate Code"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        participantTable = new JTable(tableModel);
        participantTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        participantTable.setRowHeight(28);
        participantTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) onParticipantSelected();
        });

        JScrollPane scrollTable = new JScrollPane(participantTable);
        leftPanel.add(scrollTable, BorderLayout.CENTER);

        // Left Bottom Buttons
        JPanel leftBtnBar = new JPanel(new GridLayout(2, 2, 8, 8));
        leftBtnBar.setBackground(Color.WHITE);

        btnPreview = new JButton("Preview Selected");
        btnPreview.setBackground(new Color(0x0D, 0x94, 0x88));
        btnPreview.setForeground(Color.WHITE);
        btnPreview.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        btnPreview.addActionListener(e -> onPreviewClicked());

        btnGenerateSelected = new JButton("Generate for Selected");
        btnGenerateSelected.setBackground(new Color(0x0F, 0x76, 0x6E));
        btnGenerateSelected.setForeground(Color.WHITE);
        btnGenerateSelected.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        btnGenerateSelected.addActionListener(e -> generateForSelected());

        btnGenerateAll = new JButton("Generate for All Attended");
        btnGenerateAll.setBackground(new Color(0x25, 0x63, 0xEB));
        btnGenerateAll.setForeground(Color.WHITE);
        btnGenerateAll.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        btnGenerateAll.addActionListener(e -> generateForAllAttended());

        JButton btnOpenVerify = new JButton("Verification Portal");
        btnOpenVerify.addActionListener(e -> new VerifyCertificateFrame(dataSource).setVisible(true));

        leftBtnBar.add(btnPreview);
        leftBtnBar.add(btnGenerateSelected);
        leftBtnBar.add(btnGenerateAll);
        leftBtnBar.add(btnOpenVerify);

        leftPanel.add(leftBtnBar, BorderLayout.SOUTH);
        splitPane.setLeftComponent(leftPanel);

        // Right Panel: Live Scaled Preview
        JPanel rightPanel = new JPanel(new BorderLayout(8, 8));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xE2, 0xE8, 0xF0), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JPanel previewTop = new JPanel(new BorderLayout());
        previewTop.setBackground(Color.WHITE);
        JLabel rightTitle = new JLabel("Live High-Resolution Studio Preview (3508x2480 @ 300 DPI)");
        rightTitle.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
        previewTop.add(rightTitle, BorderLayout.WEST);

        JPanel exportBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        exportBtns.setBackground(Color.WHITE);
        btnExportPdf = new JButton("Download PDF");
        btnExportPdf.setEnabled(false);
        btnExportPdf.addActionListener(e -> exportFile("pdf"));

        btnExportPng = new JButton("Download PNG");
        btnExportPng.setEnabled(false);
        btnExportPng.addActionListener(e -> exportFile("png"));

        exportBtns.add(btnExportPdf);
        exportBtns.add(btnExportPng);
        previewTop.add(exportBtns, BorderLayout.EAST);

        rightPanel.add(previewTop, BorderLayout.NORTH);

        previewPanel = new CertificatePreviewPanel();
        rightPanel.add(previewPanel, BorderLayout.CENTER);

        splitPane.setRightComponent(rightPanel);
        root.add(splitPane, BorderLayout.CENTER);

        // --- Bottom Status and Progress Bar ---
        JPanel statusBar = new JPanel(new BorderLayout(10, 0));
        statusBar.setBackground(Color.WHITE);
        statusBar.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));

        statusLabel = new JLabel("Ready.");
        statusLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        statusBar.add(statusLabel, BorderLayout.WEST);

        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setPreferredSize(new Dimension(280, 20));
        progressBar.setVisible(false);
        statusBar.add(progressBar, BorderLayout.EAST);

        root.add(statusBar, BorderLayout.SOUTH);

        setContentPane(root);
    }

    private void loadWorkshops() {
        workshopCombo.removeAllItems();
        String sql = "SELECT workshop_id, title FROM workshops ORDER BY workshop_date DESC";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                workshopCombo.addItem(new WorkshopItem(rs.getLong("workshop_id"), rs.getString("title")));
            }
        } catch (Exception e) {
            statusLabel.setText("Failed to load workshops: " + e.getMessage());
        }
    }

    private void onWorkshopSelected() {
        WorkshopItem item = (WorkshopItem) workshopCombo.getSelectedItem();
        if (item == null) return;

        // Select the workshop's designated default template if set
        try (Connection con = dataSource.getConnection()) {
            String defaultT = certificateDAO.getDefaultTemplateForWorkshop(con, item.id);
            for (int i = 0; i < templateCombo.getItemCount(); i++) {
                if (templateCombo.getItemAt(i).template.getId().equalsIgnoreCase(defaultT)) {
                    templateCombo.setSelectedIndex(i);
                    break;
                }
            }
        } catch (Exception ignored) {}

        loadParticipants(item.id);
    }

    private void loadParticipants(long workshopId) {
        tableModel.setRowCount(0);
        String sql = "SELECT r.registration_id, u.full_name, u.email, " +
                     "COALESCE(a.attendance_status, 'NOT_RECORDED') as attendance_status, " +
                     "COALESCE(c.certificate_code, c.certificate_number) as cert_code " +
                     "FROM registrations r " +
                     "JOIN users u ON r.participant_id = u.user_id " +
                     "LEFT JOIN attendance a ON r.registration_id = a.registration_id " +
                     "LEFT JOIN certificates c ON r.registration_id = c.registration_id " +
                     "WHERE r.workshop_id = ? AND r.registration_status = 'APPROVED' " +
                     "ORDER BY u.full_name ASC";

        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, workshopId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tableModel.addRow(new Object[]{
                            rs.getLong("registration_id"),
                            rs.getString("full_name"),
                            rs.getString("email"),
                            rs.getString("attendance_status"),
                            rs.getString("cert_code") != null ? rs.getString("cert_code") : "Not Issued"
                    });
                }
            }
            statusLabel.setText("Loaded " + tableModel.getRowCount() + " participant(s).");
        } catch (Exception e) {
            statusLabel.setText("Error loading participants: " + e.getMessage());
        }
    }

    private void onParticipantSelected() {
        int row = participantTable.getSelectedRow();
        if (row >= 0) {
            onPreviewClicked();
        }
    }

    private void onPreviewClicked() {
        int row = participantTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a participant from the table.", "Notice", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        long regId = (Long) tableModel.getValueAt(row, 0);
        String name = (String) tableModel.getValueAt(row, 1);
        String att = (String) tableModel.getValueAt(row, 3);
        String existingCode = (String) tableModel.getValueAt(row, 4);

        WorkshopItem ws = (WorkshopItem) workshopCombo.getSelectedItem();
        TemplateItem tItem = (TemplateItem) templateCombo.getSelectedItem();
        CertificateData.CertificateType type = (CertificateData.CertificateType) typeCombo.getSelectedItem();

        if (tItem == null || ws == null) return;

        statusLabel.setText("Rendering preview for " + name + "...");
        btnPreview.setEnabled(false);

        SwingWorker<BufferedImage, Void> worker = new SwingWorker<>() {
            private CertificateData data;

            @Override
            protected BufferedImage doInBackground() {
                String code = (!"Not Issued".equals(existingCode) && existingCode != null)
                        ? existingCode : "TRIX-PREVIEW-0001";

                data = new CertificateData(
                        name,
                        ws.title,
                        java.time.LocalDate.now().toString(),
                        "Turing Auditorium, Main Block",
                        "Department Coordinator",
                        code,
                        type,
                        java.time.LocalDate.now()
                );
                data.setVerificationUrl("http://localhost:8080/verify.html?code=" + code);

                return certificateService.getRenderer().renderToImage(tItem.template, data);
            }

            @Override
            protected void done() {
                try {
                    currentPreviewImage = get();
                    currentPreviewData = data;
                    previewPanel.setCertificateImage(currentPreviewImage);
                    btnExportPdf.setEnabled(true);
                    btnExportPng.setEnabled(true);
                    statusLabel.setText("Preview rendered successfully (" + tItem.template.getName() + ").");
                } catch (Exception e) {
                    statusLabel.setText("Preview error: " + e.getMessage());
                } finally {
                    btnPreview.setEnabled(true);
                }
            }
        };
        worker.execute();
    }

    private void generateForSelected() {
        int row = participantTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a participant to generate a certificate.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        long regId = (Long) tableModel.getValueAt(row, 0);
        String name = (String) tableModel.getValueAt(row, 1);
        String att = (String) tableModel.getValueAt(row, 3);

        if (!"PRESENT".equalsIgnoreCase(att)) {
            JOptionPane.showMessageDialog(this,
                    "Cannot generate certificate for " + name + "!\nAttendance status is '" + att + "'. Participant must be marked PRESENT.",
                    "Attendance Ineligible", JOptionPane.ERROR_MESSAGE);
            return;
        }

        TemplateItem tItem = (TemplateItem) templateCombo.getSelectedItem();
        CertificateData.CertificateType type = (CertificateData.CertificateType) typeCombo.getSelectedItem();

        setButtonsEnabled(false);
        statusLabel.setText("Issuing certificate for " + name + " in background...");

        SwingWorker<CertificateData, Void> worker = new SwingWorker<>() {
            @Override
            protected CertificateData doInBackground() throws Exception {
                return certificateService.issueCertificate(regId, tItem.template, type, 0, 1L);
            }

            @Override
            protected void done() {
                setButtonsEnabled(true);
                try {
                    CertificateData result = get();
                    JOptionPane.showMessageDialog(AdminCertificateFrame.this,
                            "Certificate Issued Successfully!\nCode: " + result.getCertificateCode() + "\nSaved to: ./certificates/",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    onWorkshopSelected();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(AdminCertificateFrame.this,
                            "Issuance Failed: " + e.getCause().getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void generateForAllAttended() {
        WorkshopItem ws = (WorkshopItem) workshopCombo.getSelectedItem();
        if (ws == null) return;

        List<Long> eligibleRegIds = new ArrayList<>();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String att = (String) tableModel.getValueAt(i, 3);
            if ("PRESENT".equalsIgnoreCase(att)) {
                eligibleRegIds.add((Long) tableModel.getValueAt(i, 0));
            }
        }

        if (eligibleRegIds.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No participants marked PRESENT found in this workshop.", "Notice", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Found " + eligibleRegIds.size() + " eligible participant(s) marked PRESENT.\nProceed with batch generation in background?",
                "Batch Certificate Issuance", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        TemplateItem tItem = (TemplateItem) templateCombo.getSelectedItem();
        CertificateData.CertificateType type = (CertificateData.CertificateType) typeCombo.getSelectedItem();

        setButtonsEnabled(false);
        progressBar.setVisible(true);
        progressBar.setValue(0);
        progressBar.setMaximum(eligibleRegIds.size());

        SwingWorker<Integer, Integer> worker = new SwingWorker<>() {
            @Override
            protected Integer doInBackground() throws Exception {
                int count = 0;
                for (int i = 0; i < eligibleRegIds.size(); i++) {
                    Long regId = eligibleRegIds.get(i);
                    try {
                        certificateService.issueCertificate(regId, tItem.template, type, 0, 1L);
                        count++;
                    } catch (Exception ex) {
                        System.err.println("Batch item failed: " + ex.getMessage());
                    }
                    publish(i + 1);
                }
                return count;
            }

            @Override
            protected void process(List<Integer> chunks) {
                int latest = chunks.get(chunks.size() - 1);
                progressBar.setValue(latest);
                statusLabel.setText("Batch processing: " + latest + " / " + eligibleRegIds.size());
            }

            @Override
            protected void done() {
                setButtonsEnabled(true);
                progressBar.setVisible(false);
                try {
                    int total = get();
                    JOptionPane.showMessageDialog(AdminCertificateFrame.this,
                            "Batch generation completed!\nTotal certificates generated: " + total,
                            "Batch Complete", JOptionPane.INFORMATION_MESSAGE);
                    onWorkshopSelected();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(AdminCertificateFrame.this, "Batch failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void setAsWorkshopDefault() {
        WorkshopItem ws = (WorkshopItem) workshopCombo.getSelectedItem();
        TemplateItem tItem = (TemplateItem) templateCombo.getSelectedItem();
        if (ws == null || tItem == null) return;

        try (Connection con = dataSource.getConnection()) {
            certificateDAO.updateDefaultTemplateForWorkshop(con, ws.id, tItem.template.getId());
            JOptionPane.showMessageDialog(this,
                    "Default template for workshop \"" + ws.title + "\" updated to: " + tItem.template.getName(),
                    "Default Assigned", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to update default template: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportFile(String format) {
        if (currentPreviewImage == null || currentPreviewData == null) return;

        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("certificate_" + currentPreviewData.getCertificateCode().replace("-", "_") + "." + format));

        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File target = chooser.getSelectedFile();
            try {
                if ("pdf".equalsIgnoreCase(format)) {
                    certificateService.getPdfExporter().exportToPdf(currentPreviewImage, currentPreviewData, target);
                } else {
                    certificateService.getRenderer().exportToPng(currentPreviewImage, target);
                }

                int choice = JOptionPane.showOptionDialog(this,
                        "File successfully exported to:\n" + target.getAbsolutePath(),
                        "Export Successful",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.INFORMATION_MESSAGE,
                        null,
                        new Object[]{"Open File", "Close"},
                        "Open File");

                if (choice == 0 && Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(target);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Export failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void setButtonsEnabled(boolean enabled) {
        btnPreview.setEnabled(enabled);
        btnGenerateSelected.setEnabled(enabled);
        btnGenerateAll.setEnabled(enabled);
        btnSetDefault.setEnabled(enabled);
    }
}
