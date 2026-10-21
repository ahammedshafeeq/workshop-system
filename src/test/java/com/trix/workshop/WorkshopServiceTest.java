package com.trix.workshop;

import com.trix.workshop.dto.WorkshopDto;
import com.trix.workshop.dto.WorkshopRequest;
import com.trix.workshop.entity.User;
import com.trix.workshop.exception.BusinessRuleException;
import com.trix.workshop.service.WorkshopService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class WorkshopServiceTest {

    @Autowired
    private WorkshopService workshopService;

    @Test
    @DisplayName("Verify end time earlier than start time is rejected")
    void testInvalidTimeThrows() {
        WorkshopRequest req = new WorkshopRequest();
        req.setTitle("Invalid Time Workshop");
        req.setCategory("AI & Machine Learning");
        req.setOrganizerName("Test Organizer");
        req.setWorkshopDate(LocalDate.now().plusDays(5));
        req.setStartTime(LocalTime.of(15, 0));
        req.setEndTime(LocalTime.of(10, 0)); // earlier than start!
        req.setVenue("Lab 1");
        req.setCapacity(20);
        req.setRegistrationDeadline(LocalDate.now().plusDays(3));

        User admin = new User();
        admin.setUserId(1L);
        admin.setEmail("admin@trix.edu");

        assertThrows(BusinessRuleException.class, () -> workshopService.createWorkshop(req, admin));
    }

    @Test
    @DisplayName("Verify all workshops retrieval returns valid items")
    void testGetAllWorkshops() {
        List<WorkshopDto> list = workshopService.getAllWorkshops(null, null, null, null);
        assertNotNull(list);
        assertFalse(list.isEmpty());
    }
}

