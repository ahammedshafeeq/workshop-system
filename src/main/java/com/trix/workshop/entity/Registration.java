package com.trix.workshop.entity;

import java.time.LocalDateTime;

public class Registration {
    private Long registrationId;
    private Long participantId;
    private Long workshopId;
    private LocalDateTime registrationDate;
    private String registrationStatus; // 'PENDING', 'APPROVED', 'REJECTED', 'CANCELLED'
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Join helper fields for display
    private String participantName;
    private String participantEmail;
    private String participantPhone;
    private String participantDepartment;
    private String participantInstitution;
    private String workshopTitle;
    private String workshopCategory;
    private String workshopDate;
    private String attendanceStatus;
    private String certificateStatus;
    private String certificateNumber;

    public Registration() {}

    public Registration(Long registrationId, Long participantId, Long workshopId,
                        LocalDateTime registrationDate, String registrationStatus,
                        String notes, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.registrationId = registrationId;
        this.participantId = participantId;
        this.workshopId = workshopId;
        this.registrationDate = registrationDate;
        this.registrationStatus = registrationStatus;
        this.notes = notes;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public Long getRegistrationId() { return registrationId; }
    public void setRegistrationId(Long registrationId) { this.registrationId = registrationId; }

    public Long getParticipantId() { return participantId; }
    public void setParticipantId(Long participantId) { this.participantId = participantId; }

    public Long getWorkshopId() { return workshopId; }
    public void setWorkshopId(Long workshopId) { this.workshopId = workshopId; }

    public LocalDateTime getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(LocalDateTime registrationDate) { this.registrationDate = registrationDate; }

    public String getRegistrationStatus() { return registrationStatus; }
    public void setRegistrationStatus(String registrationStatus) { this.registrationStatus = registrationStatus; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getParticipantName() { return participantName; }
    public void setParticipantName(String participantName) { this.participantName = participantName; }

    public String getParticipantEmail() { return participantEmail; }
    public void setParticipantEmail(String participantEmail) { this.participantEmail = participantEmail; }

    public String getParticipantPhone() { return participantPhone; }
    public void setParticipantPhone(String participantPhone) { this.participantPhone = participantPhone; }

    public String getParticipantDepartment() { return participantDepartment; }
    public void setParticipantDepartment(String participantDepartment) { this.participantDepartment = participantDepartment; }

    public String getParticipantInstitution() { return participantInstitution; }
    public void setParticipantInstitution(String participantInstitution) { this.participantInstitution = participantInstitution; }

    public String getWorkshopTitle() { return workshopTitle; }
    public void setWorkshopTitle(String workshopTitle) { this.workshopTitle = workshopTitle; }

    public String getWorkshopCategory() { return workshopCategory; }
    public void setWorkshopCategory(String workshopCategory) { this.workshopCategory = workshopCategory; }

    public String getWorkshopDate() { return workshopDate; }
    public void setWorkshopDate(String workshopDate) { this.workshopDate = workshopDate; }

    public String getAttendanceStatus() { return attendanceStatus; }
    public void setAttendanceStatus(String attendanceStatus) { this.attendanceStatus = attendanceStatus; }

    public String getCertificateStatus() { return certificateStatus; }
    public void setCertificateStatus(String certificateStatus) { this.certificateStatus = certificateStatus; }

    public String getCertificateNumber() { return certificateNumber; }
    public void setCertificateNumber(String certificateNumber) { this.certificateNumber = certificateNumber; }
}

