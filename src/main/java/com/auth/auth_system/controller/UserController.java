package com.auth.auth_system.controller;

import com.auth.auth_system.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<String>> profile() {
        return ResponseEntity.ok(new ApiResponse<>("Success", "Welcome to User Profile! You have READ_PROFILE access."));
    }
}
