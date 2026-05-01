package com.auth.auth_system.service;

import com.auth.auth_system.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TokenServiceTest {

    private TokenService tokenService;

    @BeforeEach
    void setUp() {
        tokenService = new TokenService();
    }

    @Test
    void generateAndValidateToken_Success() {
        User user = new User();
        user.setUsername("testuser");
        user.setRole("USER");

        String token = tokenService.generateToken(user);
        assertNotNull(token);

        String username = tokenService.extractUsername(token);
        assertEquals("testuser", username);

        String role = tokenService.extractRole(token);
        assertEquals("USER", role);

        assertTrue(tokenService.validateToken(token));
    }

    @Test
    void validateToken_InvalidToken_ReturnsFalse() {
        assertFalse(tokenService.validateToken("invalid.token.here"));
    }
}
