package com.trix.workshop.certificate.dao;

import com.trix.workshop.certificate.model.CertificateData;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object managing certificate persistence using PreparedStatement and try-with-resources.
 * Enforces atomic, collision-free Certificate Code generation in format: TRIX-YYYY-WWW-NNNN.
 */
public class CertificateDAO {

    /**
     * Atomically generates the next unique certificate code for a given year and workshop.
     * Guarantees duplicates are strictly impossible via sequence calculation & unique DB constraint.
     */
    public synchronized String generateUniqueCertificateCode(Connection con, int year, long workshopId) throws SQLException {
        String prefix = String.format("TRIX-%04d-%03d-", year, workshopId);
        String sql = "SELECT certificate_code FROM certificates WHERE certificate_code LIKE ? ORDER BY certificate_code DESC";

        int maxRunning = 0;
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, prefix + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String code = rs.getString("certificate_code");
                    if (code != null && code.startsWith(prefix)) {
                        String suffix = code.substring(prefix.length());
                        try {
                            int num = Integer.parseInt(suffix);
                            if (num > maxRunning) maxRunning = num;
                        } catch (NumberFormatException ignored) {}
                    }
                }
            }
        }

        int nextRunning = maxRunning + 1;
        String candidateCode = String.format("%s%04d", prefix, nextRunning);

        // Verification safety check to ensure absolute collision immunity
        String checkSql = "SELECT COUNT(*) FROM certificates WHERE certificate_code = ?";
        while (true) {
            try (PreparedStatement checkPs = con.prepareStatement(checkSql)) {
                checkPs.setString(1, candidateCode);
                try (ResultSet checkRs = checkPs.executeQuery()) {
                    if (checkRs.next() && checkRs.getInt(1) == 0) {
                        return candidateCode;
                    }
                }
            }
            nextRunning++;
            candidateCode = String.format("%s%04d", prefix, nextRunning);
        }
    }

    /**
     * Inserts a newly issued certificate record using PreparedStatement and try-with-resources.
     */
    public boolean insertCertificateRecord(Connection con,
                                          long registrationId,
                                          long participantId,
                                          long workshopId,
                                          String certificateCode,
                                          String templateId,
                                          String certificateType,
                                          Long issuedBy,
                                          String filePath) throws SQLException {

        String checkSql = "SELECT certificate_id FROM certificates WHERE registration_id = ?";
        try (PreparedStatement ps = con.prepareStatement(checkSql)) {
            ps.setLong(1, registrationId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Update existing certificate record
                    String updateSql = "UPDATE certificates SET certificate_code = ?, template_id = ?, " +
                                       "certificate_type = ?, issued_on = ?, issued_by = ?, file_path = ?, " +
                                       "certificate_status = 'ISSUED' WHERE registration_id = ?";
                    try (PreparedStatement updatePs = con.prepareStatement(updateSql)) {
                        updatePs.setString(1, certificateCode);
                        updatePs.setString(2, templateId);
                        updatePs.setString(3, certificateType);
                        updatePs.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
                        if (issuedBy != null) updatePs.setLong(5, issuedBy);
                        else updatePs.setNull(5, Types.BIGINT);
                        updatePs.setString(6, filePath);
                        updatePs.setLong(7, registrationId);
                        return updatePs.executeUpdate() > 0;
                    }
                }
            }
        }

        // Insert fresh certificate row
        String certNumber = (certificateCode != null) ? certificateCode : ("TRIX-CERT-" + System.currentTimeMillis());
        String verCode = "V-" + (certificateCode != null ? certificateCode.substring(Math.max(0, certificateCode.length() - 8)) : "SECURE");

        String insertSql = "INSERT INTO certificates (registration_id, participant_id, workshop_id, " +
                           "certificate_number, certificate_code, template_id, certificate_type, " +
                           "issued_on, issued_by, verification_code, certificate_status, " +
                           "eligibility_verified, issue_date, file_path) " +
                           "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'ISSUED', TRUE, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, registrationId);
            ps.setLong(2, participantId);
            ps.setLong(3, workshopId);
            ps.setString(4, certNumber);
            ps.setString(5, certificateCode);
            ps.setString(6, templateId != null ? templateId : "classic_gold");
            ps.setString(7, certificateType != null ? certificateType : "Participation");
            ps.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
            if (issuedBy != null) ps.setLong(9, issuedBy);
            else ps.setNull(9, Types.BIGINT);
            ps.setString(10, verCode);
            ps.setDate(11, Date.valueOf(LocalDate.now()));
            ps.setString(12, filePath);

            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Finds certificate details for verification by certificate code or verification code.
     */
    public Optional<CertificateData> findByCode(Connection con, String queryCode) throws SQLException {
        if (queryCode == null || queryCode.isBlank()) return Optional.empty();
        String trimmed = queryCode.trim();

        String sql = "SELECT c.certificate_code, c.certificate_number, c.template_id, c.certificate_type, " +
                     "c.issued_on, c.issue_date, c.file_path, " +
                     "u.full_name, w.title, w.workshop_date, w.venue, w.organizer_name " +
                     "FROM certificates c " +
                     "JOIN users u ON c.participant_id = u.user_id " +
                     "JOIN workshops w ON c.workshop_id = w.workshop_id " +
                     "WHERE LOWER(c.certificate_code) = LOWER(?) " +
                     "   OR LOWER(c.certificate_number) = LOWER(?) " +
                     "   OR LOWER(c.verification_code) = LOWER(?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, trimmed);
            ps.setString(2, trimmed);
            ps.setString(3, trimmed);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String code = rs.getString("certificate_code");
                    if (code == null || code.isBlank()) code = rs.getString("certificate_number");

                    CertificateData data = new CertificateData();
                    data.setCertificateCode(code);
                    data.setRecipientName(rs.getString("full_name"));
                    data.setWorkshopTitle(rs.getString("title"));
                    data.setWorkshopDate(rs.getString("workshop_date"));
                    data.setVenueOrMode(rs.getString("venue"));
                    data.setOrganizerName(rs.getString("organizer_name"));

                    String typeStr = rs.getString("certificate_type");
                    data.setCertificateType(CertificateData.CertificateType.fromString(typeStr));

                    Date issueDate = rs.getDate("issue_date");
                    data.setIssueDate(issueDate != null ? issueDate.toLocalDate() : LocalDate.now());

                    return Optional.of(data);
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Updates the default certificate template assigned to a workshop.
     */
    public boolean updateDefaultTemplateForWorkshop(Connection con, long workshopId, String templateId) throws SQLException {
        String sql = "UPDATE workshops SET certificate_template = ? WHERE workshop_id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, templateId);
            ps.setLong(2, workshopId);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Retrieves the default certificate template assigned to a workshop.
     */
    public String getDefaultTemplateForWorkshop(Connection con, long workshopId) throws SQLException {
        String sql = "SELECT certificate_template FROM workshops WHERE workshop_id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, workshopId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String t = rs.getString("certificate_template");
                    if (t != null && !t.isBlank()) return t;
                }
            }
        }
        return "classic_gold";
    }

    /**
     * Retrieves all certificate records belonging to a participant.
     */
    public List<CertificateData> findCertificatesByParticipant(Connection con, long participantId) throws SQLException {
        List<CertificateData> list = new ArrayList<>();
        String sql = "SELECT c.certificate_code, c.certificate_number, c.template_id, c.certificate_type, " +
                     "c.issue_date, c.file_path, " +
                     "u.full_name, w.title, w.workshop_date, w.venue, w.organizer_name " +
                     "FROM certificates c " +
                     "JOIN users u ON c.participant_id = u.user_id " +
                     "JOIN workshops w ON c.workshop_id = w.workshop_id " +
                     "WHERE c.participant_id = ? AND c.certificate_status IN ('GENERATED', 'ISSUED') " +
                     "ORDER BY c.issue_date DESC";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, participantId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String code = rs.getString("certificate_code");
                    if (code == null) code = rs.getString("certificate_number");

                    CertificateData d = new CertificateData();
                    d.setCertificateCode(code);
                    d.setRecipientName(rs.getString("full_name"));
                    d.setWorkshopTitle(rs.getString("title"));
                    d.setWorkshopDate(rs.getString("workshop_date"));
                    d.setVenueOrMode(rs.getString("venue"));
                    d.setOrganizerName(rs.getString("organizer_name"));
                    d.setCertificateType(CertificateData.CertificateType.fromString(rs.getString("certificate_type")));
                    Date dt = rs.getDate("issue_date");
                    d.setIssueDate(dt != null ? dt.toLocalDate() : LocalDate.now());

                    list.add(d);
                }
            }
        }
        return list;
    }
}
