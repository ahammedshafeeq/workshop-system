package com.trix.workshop.entity;

import java.time.LocalDateTime;

public class Attendance {
    private Long attendanceId;
    private Long registrationId;
    private Long workshopId;
    private Long participantId;
    private String attendanceStatus; // 'PRESENT', 'ABSENT', 'NOT_RECORDED'
    private LocalDateTime recordedAt;
    private String recordedBy;
    private LocalDateTime updatedAt;

    // Helper display fields
    private String participantName;
    private String participantEmail;
    private String workshopTitle;

    public Attendance() {}

    public Attendance(Long attendanceId, Long registrationId, Long workshopId, Long participantId,
                      String attendanceStatus, LocalDateTime recordedAt, String recordedBy,
                      LocalDateTime updatedAt) {
        this.attendanceId = attendanceId;
        this.registrationId = registrationId;
        this.workshopId = workshopId;
        this.participantId = participantId;
        this.attendanceStatus = attendanceStatus;
        this.recordedAt = recordedAt;
        this.recordedBy = recordedBy;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public Long getAttendanceId() { return attendanceId; }
    public void setAttendanceId(Long attendanceId) { this.attendanceId = attendanceId; }

    public Long getRegistrationId() { return registrationId; }
    public void setRegistrationId(Long registrationId) { this.registrationId = registrationId; }

    public Long getWorkshopId() { return workshopId; }
    public void setWorkshopId(Long workshopId) { this.workshopId = workshopId; }

    public Long getParticipantId() { return participantId; }
    public void setParticipantId(Long participantId) { this.participantId = participantId; }

    public String getAttendanceStatus() { return attendanceStatus; }
    public void setAttendanceStatus(String attendanceStatus) { this.attendanceStatus = attendanceStatus; }

    public LocalDateTime getRecordedAt() { return recordedAt; }
    public void setRecordedAt(LocalDateTime recordedAt) { this.recordedAt = recordedAt; }

    public String getRecordedBy() { return recordedBy; }
    public void setRecordedBy(String recordedBy) { this.recordedBy = recordedBy; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getParticipantName() { return participantName; }
    public void setParticipantName(String participantName) { this.participantName = participantName; }

    public String getParticipantEmail() { return participantEmail; }
    public void setParticipantEmail(String participantEmail) { this.participantEmail = participantEmail; }

    public String getWorkshopTitle() { return workshopTitle; }
    public void setWorkshopTitle(String workshopTitle) { this.workshopTitle = workshopTitle; }
}

