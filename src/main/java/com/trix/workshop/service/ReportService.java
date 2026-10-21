package com.trix.workshop.service;

import com.trix.workshop.dto.ReportFilterRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class ReportService {
    private final JdbcTemplate jdbcTemplate;

    public ReportService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Map<String, Object> getWorkshopSummaryReport(ReportFilterRequest filter) {
        StringBuilder sql = new StringBuilder(
                "SELECT w.workshop_id, w.title, w.category, w.workshop_date, w.venue, w.capacity, w.status, " +
                "COUNT(r.registration_id) as total_registrations, " +
                "SUM(CASE WHEN r.registration_status = 'APPROVED' THEN 1 ELSE 0 END) as approved_registrations, " +
                "SUM(CASE WHEN a.attendance_status = 'PRESENT' THEN 1 ELSE 0 END) as present_count, " +
                "COUNT(c.certificate_id) as certificates_issued " +
                "FROM workshops w " +
                "LEFT JOIN registrations r ON w.workshop_id = r.workshop_id " +
                "LEFT JOIN attendance a ON r.registration_id = a.registration_id " +
                "LEFT JOIN certificates c ON r.registration_id = c.registration_id " +
                "WHERE 1=1 "
        );
        List<Object> params = new ArrayList<>();

        if (filter.getCategory() != null && !filter.getCategory().isEmpty() && !filter.getCategory().equalsIgnoreCase("ALL")) {
            sql.append("AND w.category = ? ");
            params.add(filter.getCategory());
        }
        if (filter.getStartDate() != null) {
            sql.append("AND w.workshop_date >= ? ");
            params.add(filter.getStartDate());
        }
        if (filter.getEndDate() != null) {
            sql.append("AND w.workshop_date <= ? ");
            params.add(filter.getEndDate());
        }

        sql.append("GROUP BY w.workshop_id, w.title, w.category, w.workshop_date, w.venue, w.capacity, w.status ");
        sql.append("ORDER BY w.workshop_date DESC");

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql.toString(), params.toArray());

        Map<String, Object> response = new HashMap<>();
        response.put("reportName", "Workshop Summary Report");
        response.put("generatedAt", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        response.put("totalRecords", rows.size());
        response.put("data", rows);
        return response;
    }

    public Map<String, Object> getRegistrationReport(ReportFilterRequest filter) {
        StringBuilder sql = new StringBuilder(
                "SELECT r.registration_id, u.full_name as participant_name, u.email as participant_email, " +
                "u.department, u.institution, w.title as workshop_title, w.workshop_date, " +
                "r.registration_date, r.registration_status, r.notes " +
                "FROM registrations r " +
                "JOIN users u ON r.participant_id = u.user_id " +
                "JOIN workshops w ON r.workshop_id = w.workshop_id " +
                "WHERE 1=1 "
        );
        List<Object> params = new ArrayList<>();

        if (filter.getWorkshopId() != null) {
            sql.append("AND r.workshop_id = ? ");
            params.add(filter.getWorkshopId());
        }
        if (filter.getRegistrationStatus() != null && !filter.getRegistrationStatus().isEmpty() && !filter.getRegistrationStatus().equalsIgnoreCase("ALL")) {
            sql.append("AND r.registration_status = ? ");
            params.add(filter.getRegistrationStatus());
        }
        if (filter.getStartDate() != null) {
            sql.append("AND DATE(r.registration_date) >= ? ");
            params.add(filter.getStartDate());
        }
        if (filter.getEndDate() != null) {
            sql.append("AND DATE(r.registration_date) <= ? ");
            params.add(filter.getEndDate());
        }

        sql.append("ORDER BY r.registration_date DESC");

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql.toString(), params.toArray());

        Map<String, Object> response = new HashMap<>();
        response.put("reportName", "Participant Registration Report");
        response.put("generatedAt", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        response.put("totalRecords", rows.size());
        response.put("data", rows);
        return response;
    }

    public Map<String, Object> getAttendanceReport(ReportFilterRequest filter) {
        StringBuilder sql = new StringBuilder(
                "SELECT a.attendance_id, u.full_name as participant_name, u.email as participant_email, " +
                "u.department, w.title as workshop_title, w.workshop_date, " +
                "COALESCE(a.attendance_status, 'NOT_RECORDED') as attendance_status, " +
                "a.recorded_at, a.recorded_by " +
                "FROM registrations r " +
                "JOIN users u ON r.participant_id = u.user_id " +
                "JOIN workshops w ON r.workshop_id = w.workshop_id " +
                "LEFT JOIN attendance a ON r.registration_id = a.registration_id " +
                "WHERE r.registration_status = 'APPROVED' "
        );
        List<Object> params = new ArrayList<>();

        if (filter.getWorkshopId() != null) {
            sql.append("AND r.workshop_id = ? ");
            params.add(filter.getWorkshopId());
        }
        if (filter.getAttendanceStatus() != null && !filter.getAttendanceStatus().isEmpty() && !filter.getAttendanceStatus().equalsIgnoreCase("ALL")) {
            sql.append("AND a.attendance_status = ? ");
            params.add(filter.getAttendanceStatus());
        }

        sql.append("ORDER BY u.full_name ASC");

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql.toString(), params.toArray());

        Map<String, Object> response = new HashMap<>();
        response.put("reportName", "Attendance Report");
        response.put("generatedAt", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        response.put("totalRecords", rows.size());
        response.put("data", rows);
        return response;
    }

    public Map<String, Object> getCertificateIssuanceReport(ReportFilterRequest filter) {
        StringBuilder sql = new StringBuilder(
                "SELECT c.certificate_id, c.certificate_number, c.verification_code, " +
                "u.full_name as participant_name, u.email as participant_email, " +
                "w.title as workshop_title, w.workshop_date, " +
                "c.certificate_status, c.issue_date, c.created_at " +
                "FROM certificates c " +
                "JOIN users u ON c.participant_id = u.user_id " +
                "JOIN workshops w ON c.workshop_id = w.workshop_id " +
                "WHERE 1=1 "
        );
        List<Object> params = new ArrayList<>();

        if (filter.getWorkshopId() != null) {
            sql.append("AND c.workshop_id = ? ");
            params.add(filter.getWorkshopId());
        }
        if (filter.getCertificateStatus() != null && !filter.getCertificateStatus().isEmpty() && !filter.getCertificateStatus().equalsIgnoreCase("ALL")) {
            sql.append("AND c.certificate_status = ? ");
            params.add(filter.getCertificateStatus());
        }

        sql.append("ORDER BY c.issue_date DESC");

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql.toString(), params.toArray());

        Map<String, Object> response = new HashMap<>();
        response.put("reportName", "Certificate Issuance Report");
        response.put("generatedAt", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        response.put("totalRecords", rows.size());
        response.put("data", rows);
        return response;
    }

    public Map<String, Object> getCapacityReport() {
        String sql = "SELECT w.workshop_id, w.title, w.category, w.capacity, " +
                     "COUNT(r.registration_id) as registered_count, " +
                     "(w.capacity - COUNT(r.registration_id)) as seats_remaining, " +
                     "ROUND((COUNT(r.registration_id) / w.capacity) * 100, 1) as utilization_pct " +
                     "FROM workshops w " +
                     "LEFT JOIN registrations r ON w.workshop_id = r.workshop_id AND r.registration_status IN ('APPROVED', 'PENDING') " +
                     "GROUP BY w.workshop_id, w.title, w.category, w.capacity " +
                     "ORDER BY utilization_pct DESC";

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);

        Map<String, Object> response = new HashMap<>();
        response.put("reportName", "Workshop Capacity & Utilization Report");
        response.put("generatedAt", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        response.put("totalRecords", rows.size());
        response.put("data", rows);
        return response;
    }

    public byte[] exportToCsv(String reportType, ReportFilterRequest filter) {
        Map<String, Object> report;
        switch (reportType.toLowerCase()) {
            case "workshops":
                report = getWorkshopSummaryReport(filter);
                break;
            case "registrations":
                report = getRegistrationReport(filter);
                break;
            case "attendance":
                report = getAttendanceReport(filter);
                break;
            case "certificates":
                report = getCertificateIssuanceReport(filter);
                break;
            case "capacity":
                report = getCapacityReport();
                break;
            default:
                throw new IllegalArgumentException("Unknown report type: " + reportType);
        }

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> data = (List<Map<String, Object>>) report.get("data");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(baos, true, StandardCharsets.UTF_8);

        if (data != null && !data.isEmpty()) {
            // Header
            Set<String> headers = data.get(0).keySet();
            writer.println(String.join(",", headers));

            // Rows
            for (Map<String, Object> row : data) {
                List<String> values = new ArrayList<>();
                for (String header : headers) {
                    Object val = row.get(header);
                    values.add(escapeCsv(val != null ? val.toString() : ""));
                }
                writer.println(String.join(",", values));
            }
        } else {
            writer.println("No records found for the selected criteria.");
        }

        writer.flush();
        return baos.toByteArray();
    }

    private String escapeCsv(String input) {
        if (input == null) return "\"\"";
        // Prevent CSV Formula Injection
        String safe = input;
        if (safe.startsWith("=") || safe.startsWith("+") || safe.startsWith("-") || safe.startsWith("@")) {
            safe = "'" + safe;
        }
        return "\"" + safe.replace("\"", "\"\"") + "\"";
    }
}

