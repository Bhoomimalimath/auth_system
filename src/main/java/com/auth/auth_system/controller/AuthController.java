package com.auth.auth_system.controller;

import com.auth.auth_system.dto.ApiResponse;
import com.auth.auth_system.dto.AuthResponse;
import com.auth.auth_system.dto.LoginRequest;
import com.auth.auth_system.dto.RegisterRequest;
import com.auth.auth_system.model.User;
import com.auth.auth_system.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        User user = authService.registerUser(request);
        AuthResponse data = new AuthResponse("Registration successful", user.getUsername(), null, user.getRole());
        return ResponseEntity.ok(new ApiResponse<>("Success", data));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        String token = authService.loginUser(request);
        User user = authService.getUserByUsername(request.getUsername());
        AuthResponse data = new AuthResponse("Login successful", user.getUsername(), token, user.getRole());
        return ResponseEntity.ok(new ApiResponse<>("Success", data));
    }

    @GetMapping("/test")
    public ResponseEntity<ApiResponse<String>> test() {
        return ResponseEntity.ok(new ApiResponse<>("Success", "Auth system is running!"));
    }
}