package com.auth.auth_system.controller;

import com.auth.auth_system.dto.RegisterRequest;
import com.auth.auth_system.dto.LoginRequest;
import com.auth.auth_system.model.User;
import com.auth.auth_system.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // ✅ TEST API
    @GetMapping("/test")
    public String test() {
        return "App is working!";
    }
    //@PostMapping("/register")
    //public String register(@RequestBody RegisterRequest request) {
      //  return "CHECK RESPONSE WORKING";
    //}

    // ✅ REGISTER
    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest request) {

        User user = new User();
        user.setEmail(request.email);
        user.setPhone(request.phone);

        // IMPORTANT: encrypt password
        user.setPassword(passwordEncoder.encode(request.password));

        userRepository.save(user);

        return "User saved: " + request.email;
    }

    // ✅ LOGIN (this is Step 3)
    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) {

        System.out.println("Login API called");
        System.out.println("Email: " + request.email);

        User user = userRepository.findByEmail(request.email).orElse(null);

        if (user == null) {
            return "User not found";
        }

        System.out.println("User found");

        if (request.password == null || user.getPassword() == null) {
            return "Password is null";
        }

        if (!passwordEncoder.matches(request.password, user.getPassword())) {
            return "Invalid password";
        }

        return "Login successful";
    }
}