package com.trix.workshop.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

public class WorkshopDto {
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
    private String status;
    private Integer registeredCount;
    private Integer availableSeats;
    private Boolean isRegistrationOpen;
    private Boolean userRegistered;
    private String userRegistrationStatus;
    private LocalDateTime createdAt;

    public WorkshopDto() {}

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

    public Integer getRegisteredCount() { return registeredCount; }
    public void setRegisteredCount(Integer registeredCount) { this.registeredCount = registeredCount; }

    public Integer getAvailableSeats() { return availableSeats; }
    public void setAvailableSeats(Integer availableSeats) { this.availableSeats = availableSeats; }

    public Boolean getIsRegistrationOpen() { return isRegistrationOpen; }
    public void setIsRegistrationOpen(Boolean isRegistrationOpen) { this.isRegistrationOpen = isRegistrationOpen; }

    public Boolean getUserRegistered() { return userRegistered; }
    public void setUserRegistered(Boolean userRegistered) { this.userRegistered = userRegistered; }

    public String getUserRegistrationStatus() { return userRegistrationStatus; }
    public void setUserRegistrationStatus(String userRegistrationStatus) { this.userRegistrationStatus = userRegistrationStatus; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

