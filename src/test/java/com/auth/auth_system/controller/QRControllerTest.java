package com.auth.auth_system.controller;

import com.auth.auth_system.model.User;
import com.auth.auth_system.service.AuthService;
import com.auth.auth_system.service.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import redis.embedded.RedisServer;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class QRControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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
        } catch (Exception e) {}
    }

    @AfterEach
    void tearDown() {
        if (redisServer != null) redisServer.stop();
    }

    @Test
    void testFullQRFlow() throws Exception {
        // 1. Laptop initiates QR login
        MvcResult initResult = mockMvc.perform(post("/auth/qr/init"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.sessionId").exists())
                .andExpect(jsonPath("$.data.qrCode").exists())
                .andReturn();

        String content = initResult.getResponse().getContentAsString();
        Map<String, Object> resMap = objectMapper.readValue(content, Map.class);
        String sessionId = (String) ((Map) resMap.get("data")).get("sessionId");

        // 2. Before approval, check status
        mockMvc.perform(get("/auth/qr/status").param("sessionId", sessionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("PENDING"));

        // 3. Phone approves (mock authenticated user)
        Map<String, String> approveReq = new HashMap<>();
        approveReq.put("sessionId", sessionId);

        mockMvc.perform(post("/auth/qr/approve")
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("phone_user").roles("USER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(approveReq)))
                .andExpect(status().isOk());

        // 4. Laptop polls again - should get token
        User mockUser = new User();
        mockUser.setUsername("phone_user");
        when(authService.getUserByUsername("phone_user")).thenReturn(mockUser);
        when(tokenService.generateToken(any())).thenReturn("issued_jwt_token");

        mockMvc.perform(get("/auth/qr/status").param("sessionId", sessionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("VERIFIED"))
                .andExpect(jsonPath("$.data.token").value("issued_jwt_token"));

        // 5. Re-check status - should be expired/deleted
        mockMvc.perform(get("/auth/qr/status").param("sessionId", sessionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("EXPIRED"));
    }

    @Test
    void testApproveWithoutAuth_Returns401() throws Exception {
        Map<String, String> approveReq = new HashMap<>();
        approveReq.put("sessionId", "some_id");

        mockMvc.perform(post("/auth/qr/approve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(approveReq)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testInvalidSessionStatus() throws Exception {
        mockMvc.perform(get("/auth/qr/status").param("sessionId", "invalid_id"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("EXPIRED"));
    }
}
