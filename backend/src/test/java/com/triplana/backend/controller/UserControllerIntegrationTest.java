package com.triplana.backend.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.http.MediaType;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.triplana.backend.dto.request.LoginRequest;
import com.triplana.backend.dto.request.UpdateProfileRequest;
import com.triplana.backend.entity.User;
import com.triplana.backend.repository.UserRepository;

import jakarta.servlet.http.Cookie;
import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class UserControllerIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;


    /* PUT /api/users/profile */

    @Test
    void updateProfile_ValidNewUsername_UserIsUpdated() throws Exception {
        User existingUser = User.builder()
                .username("Existing user")
                .email("taken@email.com")
                .passwordHash(passwordEncoder.encode("Password1!"))
                .verified(true)
                .build();

        userRepository.save(existingUser);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("taken@email.com");
        loginRequest.setPassword("Password1!");

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        Cookie sessionCookie =
                loginResult.getResponse().getCookie("SESSION");

        UpdateProfileRequest updateRequest = new UpdateProfileRequest();
        updateRequest.setUsername("New valid username");

        mockMvc.perform(put("/api/users/profile")
                        .cookie(sessionCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk());

        User newUserChanges = userRepository.findByEmailIgnoreCase("taken@email.com").get();

        assertEquals(newUserChanges.getUsername(), "New valid username");
    }

    @Test
    void updateProfile_NotLoggedIn_ReturnsUnauthorized() throws Exception {
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setUsername("New username");

        mockMvc.perform(put("/api/users/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updateProfile_UsernameEmpty_ReturnsBadRequest() throws Exception {
        User existingUser = User.builder()
                .username("Existing user")
                .email("taken@email.com")
                .passwordHash(passwordEncoder.encode("Password1!"))
                .verified(true)
                .build();

        userRepository.save(existingUser);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("taken@email.com");
        loginRequest.setPassword("Password1!");

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        Cookie sessionCookie =
                loginResult.getResponse().getCookie("SESSION");

        UpdateProfileRequest updateRequest = new UpdateProfileRequest();
        updateRequest.setUsername("");

        mockMvc.perform(put("/api/users/profile")
                        .cookie(sessionCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest());

        User newUserChanges = userRepository.findByEmailIgnoreCase("taken@email.com").get();

        assertEquals(newUserChanges.getUsername(), "Existing user");
    }
}
