package com.auth.auth_system.controller;

import com.auth.auth_system.dto.AuthResponse;
import com.auth.auth_system.dto.LoginRequest;
import com.auth.auth_system.dto.RegisterRequest;
import com.auth.auth_system.model.User;
import com.auth.auth_system.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        try {
            User user = authService.registerUser(request);
            return ResponseEntity.ok(new AuthResponse("Registration successful", user.getUsername()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new AuthResponse(e.getMessage(), null));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        try {
            User user = authService.loginUser(request);
            return ResponseEntity.ok(new AuthResponse("Login successful", user.getUsername()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(new AuthResponse(e.getMessage(), null));
        }
    }

    @GetMapping("/test")
    public String test() {
        return "Auth system is running!";
    }
}