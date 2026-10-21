package com.trix.workshop.entity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

public class Workshop {
    private Long workshopId;
    private String title;
    private String description;
    private String category;
    private String organizerName;
    private Long organizerId;
    private LocalDate workshopDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String venue;
    private Integer capacity;
    private LocalDate registrationDeadline;
    private String status; // 'UPCOMING', 'ONGOING', 'COMPLETED', 'CANCELLED'
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Workshop() {}

    public Workshop(Long workshopId, String title, String description, String category,
                    String organizerName, Long organizerId, LocalDate workshopDate,
                    LocalTime startTime, LocalTime endTime, String venue, Integer capacity,
                    LocalDate registrationDeadline, String status,
                    LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.workshopId = workshopId;
        this.title = title;
        this.description = description;
        this.category = category;
        this.organizerName = organizerName;
        this.organizerId = organizerId;
        this.workshopDate = workshopDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.venue = venue;
        this.capacity = capacity;
        this.registrationDeadline = registrationDeadline;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public Long getWorkshopId() { return workshopId; }
    public void setWorkshopId(Long workshopId) { this.workshopId = workshopId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getOrganizerName() { return organizerName; }
    public void setOrganizerName(String organizerName) { this.organizerName = organizerName; }

    public Long getOrganizerId() { return organizerId; }
    public void setOrganizerId(Long organizerId) { this.organizerId = organizerId; }

    public LocalDate getWorkshopDate() { return workshopDate; }
    public void setWorkshopDate(LocalDate workshopDate) { this.workshopDate = workshopDate; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }

    public String getVenue() { return venue; }
    public void setVenue(String venue) { this.venue = venue; }

    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }

    public LocalDate getRegistrationDeadline() { return registrationDeadline; }
    public void setRegistrationDeadline(LocalDate registrationDeadline) { this.registrationDeadline = registrationDeadline; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

