package com.trix.workshop.controller;

import com.trix.workshop.dto.ApiResponse;
import com.trix.workshop.dto.LoginRequest;
import com.trix.workshop.dto.RegisterRequest;
import com.trix.workshop.dto.UserDto;
import com.trix.workshop.service.AuthService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserDto>> login(@Valid @RequestBody LoginRequest request, HttpSession session) {
        UserDto user = authService.login(request, session);
        return ResponseEntity.ok(ApiResponse.success("Login successful", user));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserDto>> register(@Valid @RequestBody RegisterRequest request, HttpSession session) {
        UserDto user = authService.register(request, session);
        return ResponseEntity.ok(ApiResponse.success("Account created successfully", user));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpSession session) {
        authService.logout(session);
        return ResponseEntity.ok(ApiResponse.success("Logged out successfully", null));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserDto>> getCurrentUser(HttpSession session) {
        UserDto user = authService.getCurrentUserDto(session);
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UserDto>> updateProfile(@RequestBody UserDto dto, HttpSession session) {
        UserDto updated = authService.updateProfile(dto, session);
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", updated));
    }
}

