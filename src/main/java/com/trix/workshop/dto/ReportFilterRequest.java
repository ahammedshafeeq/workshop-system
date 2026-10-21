package com.trix.workshop.dto;

import java.time.LocalDate;

public class ReportFilterRequest {
    private Long workshopId;
    private String category;
    private String registrationStatus;
    private String attendanceStatus;
    private String certificateStatus;
    private LocalDate startDate;
    private LocalDate endDate;

    public ReportFilterRequest() {}

    public Long getWorkshopId() { return workshopId; }
    public void setWorkshopId(Long workshopId) { this.workshopId = workshopId; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getRegistrationStatus() { return registrationStatus; }
    public void setRegistrationStatus(String registrationStatus) { this.registrationStatus = registrationStatus; }

    public String getAttendanceStatus() { return attendanceStatus; }
    public void setAttendanceStatus(String attendanceStatus) { this.attendanceStatus = attendanceStatus; }

    public String getCertificateStatus() { return certificateStatus; }
    public void setCertificateStatus(String certificateStatus) { this.certificateStatus = certificateStatus; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
}

