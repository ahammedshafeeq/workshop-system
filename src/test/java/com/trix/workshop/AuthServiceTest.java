package com.trix.workshop;

import com.trix.workshop.dto.LoginRequest;
import com.trix.workshop.dto.UserDto;
import com.trix.workshop.exception.BusinessRuleException;
import com.trix.workshop.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Test
    @DisplayName("Verify valid admin login succeeds and sets session")
    void testAdminLoginSuccess() {
        MockHttpSession session = new MockHttpSession();
        LoginRequest req = new LoginRequest("admin@trix.edu", "Admin@123");

        UserDto user = authService.login(req, session);

        assertNotNull(user);
        assertEquals("admin@trix.edu", user.getEmail());
        assertEquals("ADMIN", user.getRole());
        assertNotNull(session.getAttribute(AuthService.SESSION_USER_KEY));
    }

    @Test
    @DisplayName("Verify invalid password throws BusinessRuleException")
    void testInvalidPasswordThrows() {
        MockHttpSession session = new MockHttpSession();
        LoginRequest req = new LoginRequest("admin@trix.edu", "WrongPassword123");

        assertThrows(BusinessRuleException.class, () -> authService.login(req, session));
    }
}

