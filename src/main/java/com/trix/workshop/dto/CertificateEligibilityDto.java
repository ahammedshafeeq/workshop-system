package com.trix.workshop.dto;

public class CertificateEligibilityDto {
    private Long registrationId;
    private Long participantId;
    private String participantName;
    private String participantEmail;
    private String department;
    private String institution;
    private Long workshopId;
    private String workshopTitle;
    private String registrationStatus;
    private String attendanceStatus;
    private String eligibilityStatus; // 'ELIGIBLE', 'AWAITING_ATTENDANCE', 'NOT_ELIGIBLE', 'GENERATED', 'ISSUED'
    private String reason;
    private Long certificateId;
    private String certificateNumber;
    private String verificationCode;

    public CertificateEligibilityDto() {}

    // Getters and Setters
    public Long getRegistrationId() { return registrationId; }
    public void setRegistrationId(Long registrationId) { this.registrationId = registrationId; }

    public Long getParticipantId() { return participantId; }
    public void setParticipantId(Long participantId) { this.participantId = participantId; }

    public String getParticipantName() { return participantName; }
    public void setParticipantName(String participantName) { this.participantName = participantName; }

    public String getParticipantEmail() { return participantEmail; }
    public void setParticipantEmail(String participantEmail) { this.participantEmail = participantEmail; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getInstitution() { return institution; }
    public void setInstitution(String institution) { this.institution = institution; }

    public Long getWorkshopId() { return workshopId; }
    public void setWorkshopId(Long workshopId) { this.workshopId = workshopId; }

    public String getWorkshopTitle() { return workshopTitle; }
    public void setWorkshopTitle(String workshopTitle) { this.workshopTitle = workshopTitle; }

    public String getRegistrationStatus() { return registrationStatus; }
    public void setRegistrationStatus(String registrationStatus) { this.registrationStatus = registrationStatus; }

    public String getAttendanceStatus() { return attendanceStatus; }
    public void setAttendanceStatus(String attendanceStatus) { this.attendanceStatus = attendanceStatus; }

    public String getEligibilityStatus() { return eligibilityStatus; }
    public void setEligibilityStatus(String eligibilityStatus) { this.eligibilityStatus = eligibilityStatus; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public Long getCertificateId() { return certificateId; }
    public void setCertificateId(Long certificateId) { this.certificateId = certificateId; }

    public String getCertificateNumber() { return certificateNumber; }
    public void setCertificateNumber(String certificateNumber) { this.certificateNumber = certificateNumber; }

    public String getVerificationCode() { return verificationCode; }
    public void setVerificationCode(String verificationCode) { this.verificationCode = verificationCode; }
}

