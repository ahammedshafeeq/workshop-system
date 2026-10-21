package com.trix.workshop;

import com.trix.workshop.dto.CertificateEligibilityDto;
import com.trix.workshop.repository.CertificateRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CertificateEligibilityTest {

    @Autowired
    private CertificateRepository certificateRepository;

    @Test
    @DisplayName("Verify automated certificate eligibility logic for workshop 4")
    void testEligibilityEvaluation() {
        // Workshop 4 is completed and pre-seeded with various states
        List<CertificateEligibilityDto> list = certificateRepository.findEligibilityByWorkshop(4L);
        assertNotNull(list);
        assertFalse(list.isEmpty(), "Eligibility list should not be empty");

        // Verify that Rahul Nair has ISSUED status
        boolean foundIssued = list.stream().anyMatch(e -> "ISSUED".equalsIgnoreCase(e.getEligibilityStatus()) || "GENERATED".equalsIgnoreCase(e.getEligibilityStatus()));
        assertTrue(foundIssued, "At least one participant should have generated/issued certificate");

        // Verify that absent participant is NOT_ELIGIBLE with descriptive reason
        boolean foundAbsent = list.stream().anyMatch(e -> "NOT_ELIGIBLE".equalsIgnoreCase(e.getEligibilityStatus()) && e.getReason().contains("Absent"));
        assertTrue(foundAbsent, "Absent participant must be evaluated as NOT_ELIGIBLE with reason mentioning Absent");
    }
}

