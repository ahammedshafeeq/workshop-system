package com.trix.workshop.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Certificate {
    private Long certificateId;
    private Long registrationId;
    private Long participantId;
    private Long workshopId;
    private String certificateNumber;
    private String verificationCode;
    private String certificateStatus; // 'GENERATED', 'ISSUED', 'REVOKED'
    private Boolean eligibilityVerified;
    private LocalDate issueDate;
    private String filePath;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Display helper fields
    private String participantName;
    private String participantEmail;
    private String workshopTitle;
    private String workshopDate;
    private String venue;

    public Certificate() {}

    public Certificate(Long certificateId, Long registrationId, Long participantId, Long workshopId,
                       String certificateNumber, String verificationCode, String certificateStatus,
                       Boolean eligibilityVerified, LocalDate issueDate, String filePath,
                       LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.certificateId = certificateId;
        this.registrationId = registrationId;
        this.participantId = participantId;
        this.workshopId = workshopId;
        this.certificateNumber = certificateNumber;
        this.verificationCode = verificationCode;
        this.certificateStatus = certificateStatus;
        this.eligibilityVerified = eligibilityVerified;
        this.issueDate = issueDate;
        this.filePath = filePath;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public Long getCertificateId() { return certificateId; }
    public void setCertificateId(Long certificateId) { this.certificateId = certificateId; }

    public Long getRegistrationId() { return registrationId; }
    public void setRegistrationId(Long registrationId) { this.registrationId = registrationId; }

    public Long getParticipantId() { return participantId; }
    public void setParticipantId(Long participantId) { this.participantId = participantId; }

    public Long getWorkshopId() { return workshopId; }
    public void setWorkshopId(Long workshopId) { this.workshopId = workshopId; }

    public String getCertificateNumber() { return certificateNumber; }
    public void setCertificateNumber(String certificateNumber) { this.certificateNumber = certificateNumber; }

    public String getVerificationCode() { return verificationCode; }
    public void setVerificationCode(String verificationCode) { this.verificationCode = verificationCode; }

    public String getCertificateStatus() { return certificateStatus; }
    public void setCertificateStatus(String certificateStatus) { this.certificateStatus = certificateStatus; }

    public Boolean getEligibilityVerified() { return eligibilityVerified; }
    public void setEligibilityVerified(Boolean eligibilityVerified) { this.eligibilityVerified = eligibilityVerified; }

    public LocalDate getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDate issueDate) { this.issueDate = issueDate; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getParticipantName() { return participantName; }
    public void setParticipantName(String participantName) { this.participantName = participantName; }

    public String getParticipantEmail() { return participantEmail; }
    public void setParticipantEmail(String participantEmail) { this.participantEmail = participantEmail; }

    public String getWorkshopTitle() { return workshopTitle; }
    public void setWorkshopTitle(String workshopTitle) { this.workshopTitle = workshopTitle; }

    public String getWorkshopDate() { return workshopDate; }
    public void setWorkshopDate(String workshopDate) { this.workshopDate = workshopDate; }

    public String getVenue() { return venue; }
    public void setVenue(String venue) { this.venue = venue; }
}

