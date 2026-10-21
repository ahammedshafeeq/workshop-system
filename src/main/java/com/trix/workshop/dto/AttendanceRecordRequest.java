package com.trix.workshop.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class AttendanceRecordRequest {
    @NotNull(message = "Registration ID is required")
    private Long registrationId;

    @NotBlank(message = "Attendance status is required")
    private String status; // 'PRESENT', 'ABSENT', 'NOT_RECORDED'

    public AttendanceRecordRequest() {}

    public AttendanceRecordRequest(Long registrationId, String status) {
        this.registrationId = registrationId;
        this.status = status;
    }

    public Long getRegistrationId() { return registrationId; }
    public void setRegistrationId(Long registrationId) { this.registrationId = registrationId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}

