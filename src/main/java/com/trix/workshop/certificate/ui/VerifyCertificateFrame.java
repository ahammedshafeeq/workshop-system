package com.trix.workshop.certificate.ui;

import com.trix.workshop.certificate.dao.CertificateDAO;
import com.trix.workshop.certificate.model.CertificateData;

import javax.sql.DataSource;
import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.util.Optional;

/**
 * Public Verification Screen for checking certificate authenticity against the database.
 * Displays participant name, workshop title, date, venue, and authenticated VALID or NOT FOUND badge.
 */
public class VerifyCertificateFrame extends JFrame {

    private final DataSource dataSource;
    private final CertificateDAO certificateDAO;

    private JTextField codeField;
    private JPanel resultCard;
    private JLabel statusBadge;
    private JLabel nameLabel;
    private JLabel workshopLabel;
    private JLabel dateLabel;
    private JLabel venueLabel;
    private JLabel typeLabel;

    public VerifyCertificateFrame(DataSource dataSource) {
        this.dataSource = dataSource;
        this.certificateDAO = new CertificateDAO();

        setTitle("TRIX Workshops • Public Certificate Verification Portal");
        setSize(780, 560);
        setMinimumSize(new Dimension(650, 480));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initUI();
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout(14, 14));
        root.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));
        root.setBackground(new Color(0xF8, 0xFA, 0xFC));

        // Header Title
        JPanel header = new JPanel(new GridLayout(2, 1, 4, 4));
        header.setBackground(root.getBackground());
        JLabel mainTitle = new JLabel("Certificate Authenticity Verification Portal");
        mainTitle.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
        mainTitle.setForeground(new Color(0x0F, 0x17, 0x2A));

        JLabel subTitle = new JLabel("Enter the Certificate ID or Verification Code from the QR scan to validate official academic credentials.");
        subTitle.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        subTitle.setForeground(new Color(0x64, 0x74, 0x8B));

        header.add(mainTitle);
        header.add(subTitle);
        root.add(header, BorderLayout.NORTH);

        // Center Content
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBackground(root.getBackground());

        // Input Form Box
        JPanel inputBar = new JPanel(new BorderLayout(10, 0));
        inputBar.setBackground(Color.WHITE);
        inputBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xCB, 0xD5, 0xE1), 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 8)
        ));
        inputBar.setMaximumSize(new Dimension(720, 50));

        codeField = new JTextField();
        codeField.setFont(new Font(Font.MONOSPACED, Font.BOLD, 15));
        codeField.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        codeField.addActionListener(e -> performVerification());

        JButton verifyBtn = new JButton("Verify Authenticity");
        verifyBtn.setBackground(new Color(0x0D, 0x94, 0x88));
        verifyBtn.setForeground(Color.WHITE);
        verifyBtn.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        verifyBtn.addActionListener(e -> performVerification());

        inputBar.add(codeField, BorderLayout.CENTER);
        inputBar.add(verifyBtn, BorderLayout.EAST);

        center.add(inputBar);
        center.add(Box.createRigidArea(new Dimension(0, 18)));

        // Result Details Card
        resultCard = new JPanel(new GridLayout(6, 1, 8, 8));
        resultCard.setBackground(Color.WHITE);
        resultCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xE2, 0xE8, 0xF0), 1),
                BorderFactory.createEmptyBorder(18, 20, 18, 20)
        ));
        resultCard.setMaximumSize(new Dimension(720, 260));
        resultCard.setVisible(false);

        statusBadge = new JLabel("STATUS: VALID", SwingConstants.CENTER);
        statusBadge.setOpaque(true);
        statusBadge.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        statusBadge.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));

        nameLabel = new JLabel("Participant: -");
        nameLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));

        workshopLabel = new JLabel("Workshop: -");
        dateLabel = new JLabel("Date: -");
        venueLabel = new JLabel("Venue: -");
        typeLabel = new JLabel("Credential Type: -");

        resultCard.add(statusBadge);
        resultCard.add(nameLabel);
        resultCard.add(workshopLabel);
        resultCard.add(dateLabel);
        resultCard.add(venueLabel);
        resultCard.add(typeLabel);

        center.add(resultCard);
        root.add(center, BorderLayout.CENTER);

        // Footer Note
        JLabel footerNote = new JLabel("APJ Abdul Kalam Technological University • Department of Computer Science & Engineering", SwingConstants.CENTER);
        footerNote.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        footerNote.setForeground(new Color(0x94, 0xA3, 0xB8));
        root.add(footerNote, BorderLayout.SOUTH);

        setContentPane(root);
    }

    private void performVerification() {
        String code = codeField.getText().trim();
        if (code.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a certificate or verification code.", "Input Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection con = dataSource.getConnection()) {
            Optional<CertificateData> resultOpt = certificateDAO.findByCode(con, code);

            resultCard.setVisible(true);

            if (resultOpt.isPresent()) {
                CertificateData d = resultOpt.get();
                statusBadge.setText("✓ VALID AUTHENTICATED CERTIFICATE");
                statusBadge.setBackground(new Color(0xDC, 0xFC, 0xE7));
                statusBadge.setForeground(new Color(0x16, 0x65, 0x34));

                nameLabel.setText("Recipient: " + d.getRecipientName());
                workshopLabel.setText("Workshop: " + d.getWorkshopTitle());
                dateLabel.setText("Issued On: " + d.getFormattedIssueDate());
                venueLabel.setText("Venue: " + d.getVenueOrMode());
                typeLabel.setText("Credential Type: Certificate of " + d.getCertificateType().getDisplayName());
            } else {
                statusBadge.setText("✗ NOT FOUND / INVALID CERTIFICATE");
                statusBadge.setBackground(new Color(0xFE, 0xE2, 0xE2));
                statusBadge.setForeground(new Color(0x99, 0x1B, 0x1B));

                nameLabel.setText("No official record matching code: " + code);
                workshopLabel.setText("Reason: Unissued, revoked, or non-existent certificate credential.");
                dateLabel.setText("");
                venueLabel.setText("");
                typeLabel.setText("");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database error during verification: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
