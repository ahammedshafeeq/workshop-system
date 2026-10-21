package com.trix.workshop.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class CertificateDto {
    private Long certificateId;
    private Long registrationId;
    private Long participantId;
    private String participantName;
    private String participantEmail;
    private Long workshopId;
    private String workshopTitle;
    private String workshopDate;
    private String venue;
    private String certificateNumber;
    private String verificationCode;
    private String certificateStatus;
    private Boolean eligibilityVerified;
    private LocalDate issueDate;
    private String downloadUrl;
    private LocalDateTime createdAt;

    public CertificateDto() {}

    // Getters and Setters
    public Long getCertificateId() { return certificateId; }
    public void setCertificateId(Long certificateId) { this.certificateId = certificateId; }

    public Long getRegistrationId() { return registrationId; }
    public void setRegistrationId(Long registrationId) { this.registrationId = registrationId; }

    public Long getParticipantId() { return participantId; }
    public void setParticipantId(Long participantId) { this.participantId = participantId; }

    public String getParticipantName() { return participantName; }
    public void setParticipantName(String participantName) { this.participantName = participantName; }

    public String getParticipantEmail() { return participantEmail; }
    public void setParticipantEmail(String participantEmail) { this.participantEmail = participantEmail; }

    public Long getWorkshopId() { return workshopId; }
    public void setWorkshopId(Long workshopId) { this.workshopId = workshopId; }

    public String getWorkshopTitle() { return workshopTitle; }
    public void setWorkshopTitle(String workshopTitle) { this.workshopTitle = workshopTitle; }

    public String getWorkshopDate() { return workshopDate; }
    public void setWorkshopDate(String workshopDate) { this.workshopDate = workshopDate; }

    public String getVenue() { return venue; }
    public void setVenue(String venue) { this.venue = venue; }

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

    public String getDownloadUrl() { return downloadUrl; }
    public void setDownloadUrl(String downloadUrl) { this.downloadUrl = downloadUrl; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

