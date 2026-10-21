package com.trix.workshop.dto;

public class AttendanceSummaryDto {
    private Long workshopId;
    private String workshopTitle;
    private int totalRegistrations;
    private int approvedRegistrations;
    private int presentCount;
    private int absentCount;
    private int notRecordedCount;
    private double attendancePercentage; // (presentCount / approvedRegistrations) * 100

    public AttendanceSummaryDto() {}

    public Long getWorkshopId() { return workshopId; }
    public void setWorkshopId(Long workshopId) { this.workshopId = workshopId; }

    public String getWorkshopTitle() { return workshopTitle; }
    public void setWorkshopTitle(String workshopTitle) { this.workshopTitle = workshopTitle; }

    public int getTotalRegistrations() { return totalRegistrations; }
    public void setTotalRegistrations(int totalRegistrations) { this.totalRegistrations = totalRegistrations; }

    public int getApprovedRegistrations() { return approvedRegistrations; }
    public void setApprovedRegistrations(int approvedRegistrations) { this.approvedRegistrations = approvedRegistrations; }

    public int getPresentCount() { return presentCount; }
    public void setPresentCount(int presentCount) { this.presentCount = presentCount; }

    public int getAbsentCount() { return absentCount; }
    public void setAbsentCount(int absentCount) { this.absentCount = absentCount; }

    public int getNotRecordedCount() { return notRecordedCount; }
    public void setNotRecordedCount(int notRecordedCount) { this.notRecordedCount = notRecordedCount; }

    public double getAttendancePercentage() { return attendancePercentage; }
    public void setAttendancePercentage(double attendancePercentage) { this.attendancePercentage = attendancePercentage; }
}

