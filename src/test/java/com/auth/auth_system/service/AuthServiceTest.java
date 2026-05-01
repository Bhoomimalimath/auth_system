package com.auth.auth_system.service;

import com.auth.auth_system.dto.LoginRequest;
import com.auth.auth_system.dto.RegisterRequest;
import com.auth.auth_system.exception.AuthException;
import com.auth.auth_system.model.User;
import com.auth.auth_system.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registerUser_Success() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setPassword("password123");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(any())).thenReturn("hashed_password");
        when(userRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        User registered = authService.registerUser(request);

        assertNotNull(registered);
        assertEquals("testuser", registered.getUsername());
        verify(userRepository).save(any());
    }

    @Test
    void registerUser_DuplicateUser_ThrowsException() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(new User()));

        assertThrows(AuthException.class, () -> authService.registerUser(request));
    }

    @Test
    void loginUser_Success() {
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("password123");

        User user = new User();
        user.setUsername("testuser");
        user.setPassword("hashed_password");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "hashed_password")).thenReturn(true);
        when(tokenService.generateToken(user)).thenReturn("mock_token");

        String token = authService.loginUser(request);

        assertEquals("mock_token", token);
    }

    @Test
    void loginUser_WrongPassword_ThrowsException() {
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("wrong");

        User user = new User();
        user.setPassword("hashed_password");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "hashed_password")).thenReturn(false);

        assertThrows(AuthException.class, () -> authService.loginUser(request));
    }
}
