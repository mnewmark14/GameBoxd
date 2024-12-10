package com.example.gameboxd.gameboxd_backend.controller;

import com.example.gameboxd.gameboxd_backend.dto.login.LoginRequest;
import com.example.gameboxd.gameboxd_backend.dto.login.LoginResponse;
import com.example.gameboxd.gameboxd_backend.exception.InvalidCredentialsException;
import com.example.gameboxd.gameboxd_backend.model.User;
import com.example.gameboxd.gameboxd_backend.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean; // Correct import
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class) // Focuses on AuthController
@ActiveProfiles("test") // Activates 'test' profile
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService; // Correct annotation

    @Autowired
    private ObjectMapper objectMapper; // Uses Spring's ObjectMapper

    @Test
    void login_Success() throws Exception {
        // Arrange
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");

        User user = User.builder()
                .id(UUID.randomUUID())
                .username("testuser")
                .email("test@example.com")
                .passwordHash("hashedPassword")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(userService.authenticateUser("testuser", "password123")).thenReturn(user);
        when(userService.saveUser(any(User.class))).thenReturn(user);


        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").value("generated-token"));

        verify(userService, times(1)).authenticateUser("testuser", "password123");
        verify(userService, times(1)).saveUser(any(User.class));
    }

    @Test
    void login_InvalidCredentials() throws Exception {
        // Arrange
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("invaliduser");
        loginRequest.setPassword("wrongpassword");

        when(userService.authenticateUser("invaliduser", "wrongpassword"))
                .thenThrow(new InvalidCredentialsException("Invalid username or password"));

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid username or password"));

        verify(userService, times(1)).authenticateUser("invaliduser", "wrongpassword");
        verify(userService, times(0)).saveUser(any(User.class));
    }

    @Test
    void logout_Success() throws Exception {
        // Arrange
        String token = "valid-token";

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/logout")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).invalidateToken(token);
    }

    @Test
    void logout_InvalidAuthorizationHeader() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/logout")
                .header("Authorization", "InvalidHeader"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid Authorization header."));

        verify(userService, times(0)).invalidateToken(anyString());
    }

    @Test
    void logout_MissingAuthorizationHeader() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/logout"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid Authorization header."));

        verify(userService, times(0)).invalidateToken(anyString());
    }
}
