package com.triplana.backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doThrow;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.triplana.backend.dto.request.RegisterRequest;
import com.triplana.backend.entity.User;
import com.triplana.backend.exception.AuthException;
import com.triplana.backend.repository.TokenRepository;
import com.triplana.backend.repository.UserRepository;
import com.triplana.backend.util.TokenUtil;
import com.triplana.backend.validation.AuthValidator;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    
    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthValidator authValidator;

    @Mock
    private EmailService emailService;

    @Mock
    private TokenUtil tokenUtil;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest request;

    private User mockUser;
    

    @BeforeEach
    void setUp() {
        request = new RegisterRequest();
        request.setUsername("ValidUsername");
        request.setEmail("valid@email.com");
        request.setPassword("ValidPassword1!");
        request.setConfirmPassword("ValidPassword1!");

        mockUser = User.builder()
            .id(1L)
            .username("ValidUsername")
            .email("valid@email.com")
            .passwordHash("hashedPassword")
            .verified(false)
            .build();
    }


    // register()

    @Test
    void register_whenAllFieldsValid_doesNotThrow() {
        assertDoesNotThrow(() -> authService.register(request));
    }

    @Test
    void register_whenPasswordsDoNotMatch_throwsAuthException() {
        doThrow(new AuthException("confirmPassword", "Passwords don't match."))
            .when(authValidator).validatePasswordsMatch("ValidPassword1!", "DifferentPassword1!");

        request.setConfirmPassword("DifferentPassword1!");

        AuthException exception = assertThrows(AuthException.class, () ->
            authService.register(request));

        assertEquals("Passwords don't match.", exception.getMessage());
    }

    @Test
    void register_whenEmailTaken_throwsAuthException() {
        doThrow(new AuthException("email", "Email is already taken."))
            .when(authValidator).validateEmailNotTaken("taken@email.com");

        request.setEmail("taken@email.com");

        AuthException exception = assertThrows(AuthException.class, () ->
            authService.register(request));

        assertEquals("Email is already taken.", exception.getMessage());
    }
}
