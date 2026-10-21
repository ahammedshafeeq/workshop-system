package com.trix.workshop.service;

import com.trix.workshop.dto.WorkshopDto;
import com.trix.workshop.dto.WorkshopRequest;
import com.trix.workshop.entity.Registration;
import com.trix.workshop.entity.User;
import com.trix.workshop.entity.Workshop;
import com.trix.workshop.exception.BusinessRuleException;
import com.trix.workshop.exception.ResourceNotFoundException;
import com.trix.workshop.repository.RegistrationRepository;
import com.trix.workshop.repository.WorkshopRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class WorkshopService {
    private final WorkshopRepository workshopRepository;
    private final RegistrationRepository registrationRepository;
    private final AuditService auditService;

    public WorkshopService(WorkshopRepository workshopRepository,
                           RegistrationRepository registrationRepository,
                           AuditService auditService) {
        this.workshopRepository = workshopRepository;
        this.registrationRepository = registrationRepository;
        this.auditService = auditService;
    }

    @Transactional
    public WorkshopDto createWorkshop(WorkshopRequest request, User admin) {
        validateWorkshopRequest(request);

        Workshop w = new Workshop();
        w.setTitle(request.getTitle().trim());
        w.setDescription(request.getDescription() != null ? request.getDescription().trim() : "");
        w.setCategory(request.getCategory().trim());
        w.setOrganizerName(request.getOrganizerName().trim());
        w.setOrganizerId(admin != null ? admin.getUserId() : null);
        w.setWorkshopDate(request.getWorkshopDate());
        w.setStartTime(request.getStartTime());
        w.setEndTime(request.getEndTime());
        w.setVenue(request.getVenue().trim());
        w.setCapacity(request.getCapacity());
        w.setRegistrationDeadline(request.getRegistrationDeadline());
        w.setStatus(request.getStatus() != null ? request.getStatus().toUpperCase() : "UPCOMING");

        w = workshopRepository.save(w);

        auditService.log(admin != null ? admin.getEmail() : "ADMIN", "CREATE_WORKSHOP", "WORKSHOP", w.getWorkshopId(),
                "Created workshop: " + w.getTitle() + " (Capacity: " + w.getCapacity() + ")");

        return mapToDto(w, admin != null ? admin.getUserId() : null);
    }

    @Transactional
    public WorkshopDto updateWorkshop(Long workshopId, WorkshopRequest request, User admin) {
        Workshop w = workshopRepository.findById(workshopId)
                .orElseThrow(() -> new ResourceNotFoundException("Workshop not found with ID: " + workshopId));

        validateWorkshopRequest(request);

        int currentReg = workshopRepository.getRegisteredCount(workshopId);
        if (request.getCapacity() < currentReg) {
            throw new BusinessRuleException("Cannot set capacity (" + request.getCapacity() + ") below current registered count (" + currentReg + ")");
        }

        w.setTitle(request.getTitle().trim());
        w.setDescription(request.getDescription() != null ? request.getDescription().trim() : "");
        w.setCategory(request.getCategory().trim());
        w.setOrganizerName(request.getOrganizerName().trim());
        w.setWorkshopDate(request.getWorkshopDate());
        w.setStartTime(request.getStartTime());
        w.setEndTime(request.getEndTime());
        w.setVenue(request.getVenue().trim());
        w.setCapacity(request.getCapacity());
        w.setRegistrationDeadline(request.getRegistrationDeadline());
        if (request.getStatus() != null) {
            w.setStatus(request.getStatus().toUpperCase());
        }

        workshopRepository.update(w);

        auditService.log(admin != null ? admin.getEmail() : "ADMIN", "UPDATE_WORKSHOP", "WORKSHOP", w.getWorkshopId(),
                "Updated workshop: " + w.getTitle());

        return mapToDto(w, admin != null ? admin.getUserId() : null);
    }

    @Transactional
    public void deleteWorkshop(Long workshopId, User admin) {
        Workshop w = workshopRepository.findById(workshopId)
                .orElseThrow(() -> new ResourceNotFoundException("Workshop not found with ID: " + workshopId));

        workshopRepository.delete(workshopId);

        auditService.log(admin != null ? admin.getEmail() : "ADMIN", "DELETE_WORKSHOP", "WORKSHOP", workshopId,
                "Deleted workshop: " + w.getTitle());
    }

    public WorkshopDto getWorkshopById(Long workshopId, Long currentUserId) {
        Workshop w = workshopRepository.findById(workshopId)
                .orElseThrow(() -> new ResourceNotFoundException("Workshop not found with ID: " + workshopId));
        return mapToDto(w, currentUserId);
    }

    public List<WorkshopDto> getAllWorkshops(String category, String status, String search, Long currentUserId) {
        List<Workshop> list = workshopRepository.findAll(category, status, search);
        return list.stream()
                .map(w -> mapToDto(w, currentUserId))
                .collect(Collectors.toList());
    }

    private void validateWorkshopRequest(WorkshopRequest request) {
        if (!request.getEndTime().isAfter(request.getStartTime())) {
            throw new BusinessRuleException("Workshop end time must be later than start time.");
        }
        if (request.getRegistrationDeadline().isAfter(request.getWorkshopDate())) {
            throw new BusinessRuleException("Registration deadline cannot be after the workshop date.");
        }
        if (request.getCapacity() <= 0) {
            throw new BusinessRuleException("Workshop capacity must be at least 1 seat.");
        }
    }

    public WorkshopDto mapToDto(Workshop w, Long currentUserId) {
        WorkshopDto dto = new WorkshopDto();
        dto.setWorkshopId(w.getWorkshopId());
        dto.setTitle(w.getTitle());
        dto.setDescription(w.getDescription());
        dto.setCategory(w.getCategory());
        dto.setOrganizerName(w.getOrganizerName());
        dto.setOrganizerId(w.getOrganizerId());
        dto.setWorkshopDate(w.getWorkshopDate());
        dto.setStartTime(w.getStartTime());
        dto.setEndTime(w.getEndTime());
        dto.setVenue(w.getVenue());
        dto.setCapacity(w.getCapacity());
        dto.setRegistrationDeadline(w.getRegistrationDeadline());
        dto.setStatus(w.getStatus());
        dto.setCreatedAt(w.getCreatedAt());

        int regCount = workshopRepository.getRegisteredCount(w.getWorkshopId());
        dto.setRegisteredCount(regCount);
        dto.setAvailableSeats(Math.max(0, w.getCapacity() - regCount));

        boolean isNotExpired = !LocalDate.now().isAfter(w.getRegistrationDeadline());
        boolean hasSeats = dto.getAvailableSeats() > 0;
        boolean isUpcoming = "UPCOMING".equalsIgnoreCase(w.getStatus());
        dto.setIsRegistrationOpen(isNotExpired && hasSeats && isUpcoming);

        if (currentUserId != null) {
            Optional<Registration> regOpt = registrationRepository.findByParticipantAndWorkshop(currentUserId, w.getWorkshopId());
            if (regOpt.isPresent()) {
                dto.setUserRegistered(true);
                dto.setUserRegistrationStatus(regOpt.get().getRegistrationStatus());
            } else {
                dto.setUserRegistered(false);
                dto.setUserRegistrationStatus(null);
            }
        } else {
            dto.setUserRegistered(false);
            dto.setUserRegistrationStatus(null);
        }

        return dto;
    }
}

