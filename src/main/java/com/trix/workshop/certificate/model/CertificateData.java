package com.trix.workshop.certificate.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Encapsulates all data required to render a certificate across all templates.
 */
public class CertificateData {

    public enum CertificateType {
        PARTICIPATION("Participation"),
        COMPLETION("Completion"),
        APPRECIATION("Appreciation");

        private final String displayName;

        CertificateType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

        public static CertificateType fromString(String val) {
            if (val == null) return PARTICIPATION;
            for (CertificateType t : values()) {
                if (t.name().equalsIgnoreCase(val) || t.displayName.equalsIgnoreCase(val)) {
                    return t;
                }
            }
            return PARTICIPATION;
        }
    }

    private String recipientName;
    private String workshopTitle;
    private String workshopDate;
    private String venueOrMode;
    private String organizerName;
    private String certificateCode; // Format: TRIX-YYYY-WWW-NNNN
    private CertificateType certificateType;
    private LocalDate issueDate;
    private int sentenceVariant; // 0, 1, or 2 (3 alternatives per type)
    private double attendancePercentage; // e.g. 100.0, 85.0
    private String signatory1Title = "Head of Department";
    private String signatory1Name = "Dr. Anand Varma, Ph.D.";
    private String signatory2Title = "Faculty Coordinator";
    private String signatory2Name = "Prof. Lakshmi Priya, M.Tech";
    private String verificationUrl;

    public CertificateData() {
        this.certificateType = CertificateType.PARTICIPATION;
        this.issueDate = LocalDate.now();
        this.sentenceVariant = 0;
    }

    public CertificateData(String recipientName, String workshopTitle, String workshopDate,
                           String venueOrMode, String organizerName, String certificateCode,
                           CertificateType certificateType, LocalDate issueDate) {
        this.recipientName = recipientName;
        this.workshopTitle = workshopTitle;
        this.workshopDate = workshopDate;
        this.venueOrMode = venueOrMode;
        this.organizerName = organizerName;
        this.certificateCode = certificateCode;
        this.certificateType = certificateType != null ? certificateType : CertificateType.PARTICIPATION;
        this.issueDate = issueDate != null ? issueDate : LocalDate.now();
        this.sentenceVariant = 0;
    }

    /**
     * Generates the certificate's achievement body paragraph dynamically.
     * Incorporates the 3 alternative wording sentences specified in Step 6.
     */
    public String getAchievementSentence() {
        String title = (workshopTitle != null && !workshopTitle.isBlank()) ? workshopTitle : "the technical workshop";
        String date = (workshopDate != null && !workshopDate.isBlank()) ? workshopDate : getFormattedIssueDate();
        String venue = (venueOrMode != null && !venueOrMode.isBlank()) ? venueOrMode : "Department of Computer Science & Engineering";

        switch (certificateType) {
            case COMPLETION:
                return switch (sentenceVariant % 3) {
                    case 0 -> "has successfully completed all rigorous theoretical modules and hands-on laboratory sessions of \"" 
                            + title + "\" conducted on " + date + " at " + venue + ".";
                    case 1 -> "has demonstrated distinguished proficiency and fulfilled all technical curriculum requirements for \"" 
                            + title + "\" organized on " + date + " at " + venue + ".";
                    default -> "has satisfactorily accomplished all project milestones and practical evaluations in \"" 
                            + title + "\" convened on " + date + " at " + venue + ".";
                };
            case APPRECIATION:
                return switch (sentenceVariant % 3) {
                    case 0 -> "is awarded this certificate of appreciation for meritorious contributions and exceptional leadership during \"" 
                            + title + "\" held on " + date + " at " + venue + ".";
                    case 1 -> "with deep gratitude for exemplary initiative, intellectual engagement, and active contribution to \"" 
                            + title + "\" conducted on " + date + " at " + venue + ".";
                    default -> "in recognition of outstanding dedication, peer collaboration, and scholastic excellence in \"" 
                            + title + "\" held on " + date + " at " + venue + ".";
                };
            case PARTICIPATION:
            default:
                return switch (sentenceVariant % 3) {
                    case 0 -> "has actively participated in the academic workshop on \"" 
                            + title + "\" organized on " + date + " at " + venue + ".";
                    case 1 -> "has attended and enthusiastically contributed to the technical symposium on \"" 
                            + title + "\" conducted on " + date + " at " + venue + ".";
                    default -> "for valuable attendance and active involvement throughout the deliberations of \"" 
                            + title + "\" held on " + date + " at " + venue + ".";
                };
        }
    }

    public String getFormattedIssueDate() {
        if (issueDate == null) return LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM d, yyyy"));
        return issueDate.format(DateTimeFormatter.ofPattern("MMMM d, yyyy"));
    }

    // Getters and Setters
    public String getRecipientName() { return recipientName; }
    public void setRecipientName(String recipientName) { this.recipientName = recipientName; }

    public String getWorkshopTitle() { return workshopTitle; }
    public void setWorkshopTitle(String workshopTitle) { this.workshopTitle = workshopTitle; }

    public String getWorkshopDate() { return workshopDate; }
    public void setWorkshopDate(String workshopDate) { this.workshopDate = workshopDate; }

    public String getVenueOrMode() { return venueOrMode; }
    public void setVenueOrMode(String venueOrMode) { this.venueOrMode = venueOrMode; }

    public String getOrganizerName() { return organizerName; }
    public void setOrganizerName(String organizerName) { this.organizerName = organizerName; }

    public String getCertificateCode() { return certificateCode; }
    public void setCertificateCode(String certificateCode) { this.certificateCode = certificateCode; }

    public CertificateType getCertificateType() { return certificateType; }
    public void setCertificateType(CertificateType certificateType) { this.certificateType = certificateType; }

    public LocalDate getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDate issueDate) { this.issueDate = issueDate; }

    public int getSentenceVariant() { return sentenceVariant; }
    public void setSentenceVariant(int sentenceVariant) { this.sentenceVariant = sentenceVariant; }

    public double getAttendancePercentage() { return attendancePercentage; }
    public void setAttendancePercentage(double attendancePercentage) { this.attendancePercentage = attendancePercentage; }

    public String getSignatory1Title() { return signatory1Title; }
    public void setSignatory1Title(String signatory1Title) { this.signatory1Title = signatory1Title; }

    public String getSignatory1Name() { return signatory1Name; }
    public void setSignatory1Name(String signatory1Name) { this.signatory1Name = signatory1Name; }

    public String getSignatory2Title() { return signatory2Title; }
    public void setSignatory2Title(String signatory2Title) { this.signatory2Title = signatory2Title; }

    public String getSignatory2Name() { return signatory2Name; }
    public void setSignatory2Name(String signatory2Name) { this.signatory2Name = signatory2Name; }

    public String getVerificationUrl() { return verificationUrl; }
    public void setVerificationUrl(String verificationUrl) { this.verificationUrl = verificationUrl; }
}
