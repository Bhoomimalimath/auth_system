package com.auth.auth_system.controller;

import com.auth.auth_system.dto.LoginRequest;
import com.auth.auth_system.dto.RegisterRequest;
import com.auth.auth_system.model.User;
import com.auth.auth_system.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private org.springframework.data.redis.core.RedisTemplate<String, Object> redisTemplate;

    @MockBean
    private org.springframework.data.redis.connection.RedisConnectionFactory redisConnectionFactory;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void register_Success() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newuser");
        request.setPassword("password123");
        request.setRole("USER");

        User mockUser = new User();
        mockUser.setUsername("newuser");
        mockUser.setRole("USER");

        when(authService.registerUser(any())).thenReturn(mockUser);

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.data.username").value("newuser"));
    }

    @Test
    void login_Success() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("password123");

        User mockUser = new User();
        mockUser.setUsername("testuser");
        mockUser.setRole("USER");

        when(authService.loginUser(any())).thenReturn("mock_jwt_token");
        when(authService.getUserByUsername("testuser")).thenReturn(mockUser);

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token").value("mock_jwt_token"));
    }

    @Test
    void register_InvalidInput_ReturnsBadRequest() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername(""); // Invalid
        request.setPassword("123"); // Too short

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }
}
