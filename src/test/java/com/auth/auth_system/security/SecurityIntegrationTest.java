package com.auth.auth_system.security;

import com.auth.auth_system.model.User;
import com.auth.auth_system.service.TokenService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TokenService tokenService;

    @MockBean
    private org.springframework.data.redis.core.RedisTemplate<String, Object> redisTemplate;

    @MockBean
    private org.springframework.data.redis.connection.RedisConnectionFactory redisConnectionFactory;

    @Test
    void accessProtectedWithoutToken_Returns401() throws Exception {
        mockMvc.perform(get("/user/profile"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void accessUserWithUserToken_Returns200() throws Exception {
        User user = new User();
        user.setUsername("user_test");
        user.setRole("USER");
        String token = tokenService.generateToken(user);

        mockMvc.perform(get("/user/profile")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void accessAdminWithUserToken_Returns403() throws Exception {
        User user = new User();
        user.setUsername("user_test");
        user.setRole("USER");
        String token = tokenService.generateToken(user);

        mockMvc.perform(get("/admin/dashboard")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void accessAdminWithAdminToken_Returns200() throws Exception {
        User admin = new User();
        admin.setUsername("admin_test");
        admin.setRole("ADMIN");
        String token = tokenService.generateToken(admin);

        mockMvc.perform(get("/admin/dashboard")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }
}
