package com.trix.workshop.dto;

import java.time.LocalDateTime;

public class RegistrationDto {
    private Long registrationId;
    private Long participantId;
    private String participantName;
    private String participantEmail;
    private String participantPhone;
    private String participantDepartment;
    private String participantInstitution;
    private Long workshopId;
    private String workshopTitle;
    private String workshopCategory;
    private String workshopDate;
    private String workshopVenue;
    private LocalDateTime registrationDate;
    private String registrationStatus;
    private String notes;
    private String attendanceStatus;
    private String certificateStatus;
    private Long certificateId;
    private String certificateNumber;

    public RegistrationDto() {}

    // Getters and Setters
    public Long getRegistrationId() { return registrationId; }
    public void setRegistrationId(Long registrationId) { this.registrationId = registrationId; }

    public Long getParticipantId() { return participantId; }
    public void setParticipantId(Long participantId) { this.participantId = participantId; }

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

    public Long getWorkshopId() { return workshopId; }
    public void setWorkshopId(Long workshopId) { this.workshopId = workshopId; }

    public String getWorkshopTitle() { return workshopTitle; }
    public void setWorkshopTitle(String workshopTitle) { this.workshopTitle = workshopTitle; }

    public String getWorkshopCategory() { return workshopCategory; }
    public void setWorkshopCategory(String workshopCategory) { this.workshopCategory = workshopCategory; }

    public String getWorkshopDate() { return workshopDate; }
    public void setWorkshopDate(String workshopDate) { this.workshopDate = workshopDate; }

    public String getWorkshopVenue() { return workshopVenue; }
    public void setWorkshopVenue(String workshopVenue) { this.workshopVenue = workshopVenue; }

    public LocalDateTime getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(LocalDateTime registrationDate) { this.registrationDate = registrationDate; }

    public String getRegistrationStatus() { return registrationStatus; }
    public void setRegistrationStatus(String registrationStatus) { this.registrationStatus = registrationStatus; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getAttendanceStatus() { return attendanceStatus; }
    public void setAttendanceStatus(String attendanceStatus) { this.attendanceStatus = attendanceStatus; }

    public String getCertificateStatus() { return certificateStatus; }
    public void setCertificateStatus(String certificateStatus) { this.certificateStatus = certificateStatus; }

    public Long getCertificateId() { return certificateId; }
    public void setCertificateId(Long certificateId) { this.certificateId = certificateId; }

    public String getCertificateNumber() { return certificateNumber; }
    public void setCertificateNumber(String certificateNumber) { this.certificateNumber = certificateNumber; }
}

