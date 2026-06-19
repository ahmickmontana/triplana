package com.triplana.backend.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.triplana.backend.dto.request.ForgotPasswordRequest;
import com.triplana.backend.dto.request.LoginRequest;
import com.triplana.backend.dto.request.RegisterRequest;
import com.triplana.backend.dto.request.ResendVerificationRequest;
import com.triplana.backend.dto.request.ResetPasswordRequest;
import com.triplana.backend.entity.Token;
import com.triplana.backend.entity.TokenType;
import com.triplana.backend.entity.User;
import com.triplana.backend.repository.TokenRepository;
import com.triplana.backend.repository.UserRepository;
import com.triplana.backend.service.EmailService;
import com.triplana.backend.util.TokenUtil;

import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class AuthControllerIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private TokenUtil tokenUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private EmailService emailService;


    /* POST /api/auth/register */

    @Test
    void register_whenValidInputs_userIsCreatedSuccessfully() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("Valid username");
        request.setEmail("valid@email.com");
        request.setPassword("Password1!");
        request.setConfirmPassword("Password1!");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated());

        assertTrue(userRepository.existsByEmailIgnoreCase("valid@email.com"));
    }

    @Test
    void register_whenUsernameBlank_returnsBadRequest() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("");
        request.setEmail("valid@email.com");
        request.setPassword("Password1!");
        request.setConfirmPassword("Password1!");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.username").value("Username cannot be empty."));

        assertFalse(userRepository.existsByEmailIgnoreCase("valid@email.com"));
    }

    @Test
    void register_whenUsernameWhitespace_returnsBadRequest() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername(" ");
        request.setEmail("valid@email.com");
        request.setPassword("Password1!");
        request.setConfirmPassword("Password1!");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.username").value("Username cannot be empty."));

        assertFalse(userRepository.existsByEmailIgnoreCase("valid@email.com"));
    }

    @Test
    void register_whenPasswordsDontMatch_returnsBadRequest() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("Valid username");
        request.setEmail("valid@email.com");
        request.setPassword("Password1!");
        request.setConfirmPassword("DifferentPassword1!");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.confirmPassword").value("Passwords don't match."));

        assertFalse(userRepository.existsByEmailIgnoreCase("valid@email.com"));
    }

    @Test
    void register_whenEmailTaken_returnsBadRequest() throws Exception {
        // Creating the existing user first.
        User existingUser = User.builder()
            .username("Existing user")
            .email("taken@email.com")
            .passwordHash(passwordEncoder.encode("Password1!"))
            .verified(true)
            .build();
        userRepository.save(existingUser);

        RegisterRequest request = new RegisterRequest();
        request.setUsername("Valid username");
        request.setEmail("taken@email.com");
        request.setPassword("Password1!");
        request.setConfirmPassword("Password1!");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.email").value("An account already exists under this email."));
    }

    /* POST /api/auth/register */

    @Test
    void login_whenValidCredentialsAndVerified_loginSuccessful() throws Exception {
        // Creating the existing user first.
        User existingUser = User.builder()
            .username("Existing user")
            .email("taken@email.com")
            .passwordHash(passwordEncoder.encode("Password1!"))
            .verified(true)
            .build();
        userRepository.save(existingUser);

        LoginRequest request = new LoginRequest();
        request.setEmail("taken@email.com");
        request.setPassword("Password1!");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("success"))
            .andExpect(jsonPath("$.user.email").value("taken@email.com"));
    }

    @Test
    void login_whenEmailNotAssociatedWithAccount_returnsBadRequest() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("unused@email.com");
        request.setPassword("Password1!");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Email or password is invalid."));
    }

    @Test
    void login_whenWrongPassword_returnsBadRequest() throws Exception {
        // Creating the existing user first.
        User existingUser = User.builder()
            .username("Existing user")
            .email("taken@email.com")
            .passwordHash(passwordEncoder.encode("Password1!"))
            .verified(true)
            .build();
        userRepository.save(existingUser);

        LoginRequest request = new LoginRequest();
        request.setEmail("taken@email.com");
        request.setPassword("WrongPassword1!");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Email or password is invalid."));
    }

    @Test
    void login_whenUserUnverified_returnsUnverified() throws Exception {
        // Creating the unverified user first.
        User existingUser = User.builder()
            .username("Existing user")
            .email("taken@email.com")
            .passwordHash(passwordEncoder.encode("Password1!"))
            .verified(false)
            .build();
        userRepository.save(existingUser);

        LoginRequest request = new LoginRequest();
        request.setEmail("taken@email.com");
        request.setPassword("Password1!");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("unverified"));
    }

    /* GET /api/auth/verify */

    @Test
    void verify_tokenIsValid_userIsVerified() throws Exception {
        // Creating the unverified user first.
        User existingUser = User.builder()
            .username("Existing user")
            .email("taken@email.com")
            .passwordHash(passwordEncoder.encode("Password1!"))
            .verified(false)
            .build();
        userRepository.save(existingUser);

        // Create token for the unverified user
        String rawToken = tokenUtil.generateToken();
        String hashedToken = tokenUtil.hashToken(rawToken);

        Token token = Token.builder()
            .user(existingUser)
            .tokenHash(hashedToken)
            .type(TokenType.VERIFICATION)
            .expiresAt(LocalDateTime.now().plusHours(24))
            .used(false)
            .build();
        tokenRepository.save(token);

        mockMvc.perform(get("/api/auth/verify")
                .param("token", rawToken))
            .andExpect(status().isOk());

        User updatedUser = userRepository.findByEmailIgnoreCase("taken@email.com").orElseThrow();
        assertTrue(updatedUser.isVerified());
    }

    @Test
    void verify_tokenIsExpired_returnsBadRequest() throws Exception {
        // Creating the unverified user first.
        User existingUser = User.builder()
            .username("Existing user")
            .email("taken@email.com")
            .passwordHash(passwordEncoder.encode("Password1!"))
            .verified(false)
            .build();
        userRepository.save(existingUser);

        // Create token for the unverified user
        String rawToken = tokenUtil.generateToken();
        String hashedToken = tokenUtil.hashToken(rawToken);

        Token token = Token.builder()
            .user(existingUser)
            .tokenHash(hashedToken)
            .type(TokenType.VERIFICATION)
            .expiresAt(LocalDateTime.now().minusHours(24))
            .used(false)
            .build();
        tokenRepository.save(token);

        mockMvc.perform(get("/api/auth/verify")
                .param("token", rawToken))
            .andExpect(status().isBadRequest());

        User updatedUser = userRepository.findByEmailIgnoreCase("taken@email.com").orElseThrow();
        assertFalse(updatedUser.isVerified());
    }

    @Test
    void verify_tokenIsInvalid_returnsBadRequest() throws Exception {
        // Creating the unverified user first.
        User existingUser = User.builder()
            .username("Existing user")
            .email("taken@email.com")
            .passwordHash(passwordEncoder.encode("Password1!"))
            .verified(false)
            .build();
        userRepository.save(existingUser);

        // Create token but don't save it in the repository
        String rawToken = tokenUtil.generateToken();

        mockMvc.perform(get("/api/auth/verify")
                .param("token", rawToken))
            .andExpect(status().isBadRequest());

        User updatedUser = userRepository.findByEmailIgnoreCase("taken@email.com").orElseThrow();
        assertFalse(updatedUser.isVerified());
    }

    @Test
    void verify_userAlreadyVerified_returnsBadRequest() throws Exception {
        // Creating the unverified user first.
        User existingUser = User.builder()
            .username("Existing user")
            .email("taken@email.com")
            .passwordHash(passwordEncoder.encode("Password1!"))
            .verified(true)
            .build();
        userRepository.save(existingUser);

        // Create token for the unverified user
        String rawToken = tokenUtil.generateToken();
        String hashedToken = tokenUtil.hashToken(rawToken);

        Token token = Token.builder()
            .user(existingUser)
            .tokenHash(hashedToken)
            .type(TokenType.VERIFICATION)
            .expiresAt(LocalDateTime.now().plusHours(24))
            .used(true)
            .build();
        tokenRepository.save(token);

        mockMvc.perform(get("/api/auth/verify")
                .param("token", rawToken))
            .andExpect(status().isBadRequest());
    }

    /* POST /api/auth/logout */

    @Test
    void logout_sessionIsInvalidated() throws Exception {
        mockMvc.perform(post("/api/auth/logout")
                .sessionAttr("userId", 1L))
            .andExpect(status().isOk());
    }

    /* POST /api/auth/forgot-password */

    @Test
    void forgotPassword_emailIsSent() throws Exception {
        ForgotPasswordRequest request = new ForgotPasswordRequest();
        request.setEmail("valid@email.com");

        mockMvc.perform(post("/api/auth/forgot-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk());
    }

    /* POST /api/auth/reset-password */

    @Test
    void resetPassword_NewPasswordValid_PasswordIsChanged() throws Exception {
        // Creating the unverified user first.
        User existingUser = User.builder()
            .username("Existing user")
            .email("taken@email.com")
            .passwordHash(passwordEncoder.encode("Password1!"))
            .verified(true)
            .build();
        userRepository.save(existingUser);

        // Create token for the unverified user
        String rawToken = tokenUtil.generateToken();
        String hashedToken = tokenUtil.hashToken(rawToken);

        Token token = Token.builder()
            .user(existingUser)
            .tokenHash(hashedToken)
            .type(TokenType.PASSWORD_RESET)
            .expiresAt(LocalDateTime.now().plusHours(24))
            .used(false)
            .build();
        tokenRepository.save(token);

        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setNewPassword("NewPassword1!");
        request.setConfirmPassword("NewPassword1!");
        request.setToken(rawToken);

        mockMvc.perform(post("/api/auth/reset-password")
                .param("token", rawToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk());

        User updatedUser = userRepository.findByEmailIgnoreCase("taken@email.com").orElseThrow();
        assertTrue(passwordEncoder.matches("NewPassword1!", updatedUser.getPasswordHash()));
    }

    @Test
    void resetPassword_PasswordsDontMatch_ReturnsBadRequest() throws Exception {
        // Creating the unverified user first.
        User existingUser = User.builder()
            .username("Existing user")
            .email("taken@email.com")
            .passwordHash(passwordEncoder.encode("Password1!"))
            .verified(true)
            .build();
        userRepository.save(existingUser);

        // Create token for the unverified user
        String rawToken = tokenUtil.generateToken();
        String hashedToken = tokenUtil.hashToken(rawToken);

        Token token = Token.builder()
            .user(existingUser)
            .tokenHash(hashedToken)
            .type(TokenType.PASSWORD_RESET)
            .expiresAt(LocalDateTime.now().plusHours(24))
            .used(false)
            .build();
        tokenRepository.save(token);

        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setNewPassword("NewPassword1!");
        request.setConfirmPassword("DifferentNewPassword1!");
        request.setToken(rawToken);

        mockMvc.perform(post("/api/auth/reset-password")
                .param("token", rawToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());

        User updatedUser = userRepository.findByEmailIgnoreCase("taken@email.com").orElseThrow();
        assertFalse(passwordEncoder.matches("NewPassword1!", updatedUser.getPasswordHash()));
        assertTrue(passwordEncoder.matches("Password1!", updatedUser.getPasswordHash()));
    }

    @Test
    void resetPassword_TokenIsExpired_ReturnsBadRequest() throws Exception {
        // Creating the unverified user first.
        User existingUser = User.builder()
            .username("Existing user")
            .email("taken@email.com")
            .passwordHash(passwordEncoder.encode("Password1!"))
            .verified(true)
            .build();
        userRepository.save(existingUser);

        // Create token for the unverified user
        String rawToken = tokenUtil.generateToken();
        String hashedToken = tokenUtil.hashToken(rawToken);

        Token token = Token.builder()
            .user(existingUser)
            .tokenHash(hashedToken)
            .type(TokenType.PASSWORD_RESET)
            .expiresAt(LocalDateTime.now().minusHours(24))
            .used(false)
            .build();
        tokenRepository.save(token);

        mockMvc.perform(post("/api/auth/reset-password")
                .param("token", rawToken))
            .andExpect(status().isBadRequest());

        User updatedUser = userRepository.findByEmailIgnoreCase("taken@email.com").orElseThrow();
        assertTrue(passwordEncoder.matches("Password1!", updatedUser.getPasswordHash()));
    }

     @Test
    void resetPassword_TokenIsInvalid_ReturnsBadRequest() throws Exception {
        // Creating the unverified user first.
        User existingUser = User.builder()
            .username("Existing user")
            .email("taken@email.com")
            .passwordHash(passwordEncoder.encode("Password1!"))
            .verified(true)
            .build();
        userRepository.save(existingUser);

        // Create token for the unverified user
        String rawToken = tokenUtil.generateToken();
        // Create the hash token, but don't save it in the repository

        mockMvc.perform(post("/api/auth/reset-password")
                .param("token", rawToken))
            .andExpect(status().isBadRequest());

        User updatedUser = userRepository.findByEmailIgnoreCase("taken@email.com").orElseThrow();
        assertTrue(passwordEncoder.matches("Password1!", updatedUser.getPasswordHash()));
    }

    /* POST /api/auth/resend-verification */
    
    @Test
    void resendVerification_ValidEmail_EmailIsSent() throws Exception {
        // Creating the unverified user first.
        User existingUser = User.builder()
            .username("Existing user")
            .email("taken@email.com")
            .passwordHash(passwordEncoder.encode("Password1!"))
            .verified(false)
            .build();
        userRepository.save(existingUser);

        ResendVerificationRequest request = new ResendVerificationRequest();
        request.setEmail("taken@email.com");

        mockMvc.perform(post("/api/auth/resend-verification")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk());
    }

    @Test
    void resendVerification_AccountDoesNotExist_ReturnsBadRequest() throws Exception {

        ResendVerificationRequest request = new ResendVerificationRequest();
        request.setEmail("invalid@email.com");

        mockMvc.perform(post("/api/auth/resend-verification")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.email").value("An account does not exist for this email."));
    }

    @Test
    void resendVerification_AccountAlreadyVerified_ReturnsBadRequest() throws Exception {
        // Creating the unverified user first.
        User existingUser = User.builder()
            .username("Existing user")
            .email("taken@email.com")
            .passwordHash(passwordEncoder.encode("Password1!"))
            .verified(true)
            .build();
        userRepository.save(existingUser);

        ResendVerificationRequest request = new ResendVerificationRequest();
        request.setEmail("taken@email.com");

        mockMvc.perform(post("/api/auth/resend-verification")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.email").value("Account is already verified."));
    }
}
