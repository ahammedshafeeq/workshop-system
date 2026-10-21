package com.trix.workshop.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

public class WorkshopRequest {
    @NotBlank(message = "Workshop title is required")
    private String title;

    private String description;

    @NotBlank(message = "Category is required")
    private String category;

    @NotBlank(message = "Organizer name is required")
    private String organizerName;

    @NotNull(message = "Workshop date is required")
    private LocalDate workshopDate;

    @NotNull(message = "Start time is required")
    private LocalTime startTime;

    @NotNull(message = "End time is required")
    private LocalTime endTime;

    @NotBlank(message = "Venue is required")
    private String venue;

    @NotNull(message = "Capacity is required")
    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer capacity;

    @NotNull(message = "Registration deadline is required")
    private LocalDate registrationDeadline;

    private String status = "UPCOMING";

    public WorkshopRequest() {}

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getOrganizerName() { return organizerName; }
    public void setOrganizerName(String organizerName) { this.organizerName = organizerName; }

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
}

