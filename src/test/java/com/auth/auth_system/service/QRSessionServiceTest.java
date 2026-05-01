package com.auth.auth_system.service;

import com.auth.auth_system.model.QRSession;
import com.auth.auth_system.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.RedisTemplate;
import redis.embedded.RedisServer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class QRSessionServiceTest {

    @Autowired
    private QRSessionService qrSessionService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @MockBean
    private AuthService authService;

    @MockBean
    private TokenService tokenService;

    private RedisServer redisServer;

    @BeforeEach
    void setUp() {
        try {
            redisServer = new RedisServer(6379);
            redisServer.start();
        } catch (Exception e) {
            // If already running or port busy, just continue
        }
    }

    @AfterEach
    void tearDown() {
        if (redisServer != null) {
            redisServer.stop();
        }
    }

    @Test
    void testCreateSession() {
        String sessionId = qrSessionService.createSession();
        assertNotNull(sessionId);
        
        QRSession session = qrSessionService.getStatus(sessionId);
        assertEquals("PENDING", session.getStatus());
        assertNull(session.getUsername());
    }

    @Test
    void testApproveSession() {
        String sessionId = qrSessionService.createSession();
        qrSessionService.approveSession(sessionId, "testuser");
        
        QRSession session = qrSessionService.getStatus(sessionId);
        assertEquals("VERIFIED", session.getStatus());
        assertEquals("testuser", session.getUsername());
    }

    @Test
    void testCompleteLogin() {
        String sessionId = qrSessionService.createSession();
        qrSessionService.approveSession(sessionId, "testuser");

        User mockUser = new User();
        mockUser.setUsername("testuser");
        when(authService.getUserByUsername("testuser")).thenReturn(mockUser);
        when(tokenService.generateToken(mockUser)).thenReturn("mock_token");

        String token = qrSessionService.completeLogin(sessionId);
        assertEquals("mock_token", token);
        
        // Session should be deleted
        QRSession session = qrSessionService.getStatus(sessionId);
        assertEquals("EXPIRED", session.getStatus()); // getStatus returns EXPIRED if null
    }

    @Test
    void testSessionExpiry() {
        QRSession session = qrSessionService.getStatus("non-existent");
        assertEquals("EXPIRED", session.getStatus());
    }
}
