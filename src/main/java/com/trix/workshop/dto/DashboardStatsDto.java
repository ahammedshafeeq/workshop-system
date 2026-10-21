package com.trix.workshop.dto;

import com.trix.workshop.entity.AuditLog;
import java.util.List;
import java.util.Map;

public class DashboardStatsDto {
    // Admin metrics
    private int totalWorkshops;
    private int upcomingWorkshops;
    private int ongoingWorkshops;
    private int completedWorkshops;
    private int totalParticipants;
    private int pendingRegistrations;
    private int approvedRegistrations;
    private int rejectedRegistrations;
    private int cancelledRegistrations;
    private double overallAttendanceRate;
    private int totalPresent;
    private int totalAbsent;
    private int certificatesIssued;
    private int certificatesGenerated;

    private Map<String, Integer> registrationStatusBreakdown;
    private Map<String, Integer> categoryBreakdown;
    private List<WorkshopDto> upcomingWorkshopsList;
    private List<AuditLog> recentActivities;

    // Participant metrics
    private int participantRegisteredCount;
    private int participantUpcomingCount;
    private int participantCompletedCount;
    private int participantCertificatesCount;

    public DashboardStatsDto() {}

    // Getters and Setters
    public int getTotalWorkshops() { return totalWorkshops; }
    public void setTotalWorkshops(int totalWorkshops) { this.totalWorkshops = totalWorkshops; }

    public int getUpcomingWorkshops() { return upcomingWorkshops; }
    public void setUpcomingWorkshops(int upcomingWorkshops) { this.upcomingWorkshops = upcomingWorkshops; }

    public int getOngoingWorkshops() { return ongoingWorkshops; }
    public void setOngoingWorkshops(int ongoingWorkshops) { this.ongoingWorkshops = ongoingWorkshops; }

    public int getCompletedWorkshops() { return completedWorkshops; }
    public void setCompletedWorkshops(int completedWorkshops) { this.completedWorkshops = completedWorkshops; }

    public int getTotalParticipants() { return totalParticipants; }
    public void setTotalParticipants(int totalParticipants) { this.totalParticipants = totalParticipants; }

    public int getPendingRegistrations() { return pendingRegistrations; }
    public void setPendingRegistrations(int pendingRegistrations) { this.pendingRegistrations = pendingRegistrations; }

    public int getApprovedRegistrations() { return approvedRegistrations; }
    public void setApprovedRegistrations(int approvedRegistrations) { this.approvedRegistrations = approvedRegistrations; }

    public int getRejectedRegistrations() { return rejectedRegistrations; }
    public void setRejectedRegistrations(int rejectedRegistrations) { this.rejectedRegistrations = rejectedRegistrations; }

    public int getCancelledRegistrations() { return cancelledRegistrations; }
    public void setCancelledRegistrations(int cancelledRegistrations) { this.cancelledRegistrations = cancelledRegistrations; }

    public double getOverallAttendanceRate() { return overallAttendanceRate; }
    public void setOverallAttendanceRate(double overallAttendanceRate) { this.overallAttendanceRate = overallAttendanceRate; }

    public int getTotalPresent() { return totalPresent; }
    public void setTotalPresent(int totalPresent) { this.totalPresent = totalPresent; }

    public int getTotalAbsent() { return totalAbsent; }
    public void setTotalAbsent(int totalAbsent) { this.totalAbsent = totalAbsent; }

    public int getCertificatesIssued() { return certificatesIssued; }
    public void setCertificatesIssued(int certificatesIssued) { this.certificatesIssued = certificatesIssued; }

    public int getCertificatesGenerated() { return certificatesGenerated; }
    public void setCertificatesGenerated(int certificatesGenerated) { this.certificatesGenerated = certificatesGenerated; }

    public Map<String, Integer> getRegistrationStatusBreakdown() { return registrationStatusBreakdown; }
    public void setRegistrationStatusBreakdown(Map<String, Integer> registrationStatusBreakdown) { this.registrationStatusBreakdown = registrationStatusBreakdown; }

    public Map<String, Integer> getCategoryBreakdown() { return categoryBreakdown; }
    public void setCategoryBreakdown(Map<String, Integer> categoryBreakdown) { this.categoryBreakdown = categoryBreakdown; }

    public List<WorkshopDto> getUpcomingWorkshopsList() { return upcomingWorkshopsList; }
    public void setUpcomingWorkshopsList(List<WorkshopDto> upcomingWorkshopsList) { this.upcomingWorkshopsList = upcomingWorkshopsList; }

    public List<AuditLog> getRecentActivities() { return recentActivities; }
    public void setRecentActivities(List<AuditLog> recentActivities) { this.recentActivities = recentActivities; }

    public int getParticipantRegisteredCount() { return participantRegisteredCount; }
    public void setParticipantRegisteredCount(int participantRegisteredCount) { this.participantRegisteredCount = participantRegisteredCount; }

    public int getParticipantUpcomingCount() { return participantUpcomingCount; }
    public void setParticipantUpcomingCount(int participantUpcomingCount) { this.participantUpcomingCount = participantUpcomingCount; }

    public int getParticipantCompletedCount() { return participantCompletedCount; }
    public void setParticipantCompletedCount(int participantCompletedCount) { this.participantCompletedCount = participantCompletedCount; }

    public int getParticipantCertificatesCount() { return participantCertificatesCount; }
    public void setParticipantCertificatesCount(int participantCertificatesCount) { this.participantCertificatesCount = participantCertificatesCount; }
}

