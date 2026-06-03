package com.triplana.backend.validation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.triplana.backend.entity.Token;
import com.triplana.backend.entity.TokenType;
import com.triplana.backend.exception.AuthException;
import com.triplana.backend.repository.TokenRepository;
import com.triplana.backend.repository.UserRepository;


@ExtendWith(MockitoExtension.class)
public class AuthValidatorTest {
    
    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenRepository tokenRepository;

    @InjectMocks
    private AuthValidator authValidator;

    private Token validToken;


    @BeforeEach
    void setUp() {
        validToken = Token.builder()
            .tokenHash("hashedToken")
            .type(TokenType.VERIFICATION)
            .expiresAt(LocalDateTime.now().plusHours(24))
            .used(false)
            .build();
    }


    // validatePasswordsMatch

    @Test
    void validatePasswordsMatch_whenPasswordsMatch_doesNotThrow() {
        assertDoesNotThrow(() -> authValidator.validatePasswordsMatch("Password1!", "Password1!"));
    }

    @Test
    void validatePasswordsMatch_whenPasswordsDontMatch_throwsAuthException() {
        AuthException exception = assertThrows(AuthException.class, () ->
            authValidator.validatePasswordsMatch("Password1!", "DifferentPassword1!"));

        assertEquals("Passwords don't match.", exception.getMessage());
    }


    // validateEmailNotTaken
    
    @Test
    void validateEmailNotTaken_whenEmailNotTaken_doesNotThrow() {
        when(userRepository.existsByEmail("new@email.com")).thenReturn(false);

        assertDoesNotThrow(() -> authValidator.validateEmailNotTaken("new@email.com"));
    }

    @Test
    void validateEmailNotTaken_whenEmailTaken_throwsAuthException() {
        when(userRepository.existsByEmail("taken@email.com")).thenReturn(true);

        AuthException exception = assertThrows(AuthException.class, () ->
            authValidator.validateEmailNotTaken("taken@email.com"));

        assertEquals("Email is already taken.", exception.getMessage());
    }


    // validateTokenValid

    @Test
    void validateTokenValid_whenTokenValid_doesNotThrow() {
        when(tokenRepository.findByTokenHash("hashedToken")).thenReturn(Optional.of(validToken));

        Token result = authValidator.validateTokenValid("hashedToken");

        assertEquals(validToken, result);
    }

    @Test
    void validateTokenValid_whenTokenNotFound_throwsAuthException() {
        when(tokenRepository.findByTokenHash("notValidToken")).thenReturn(Optional.empty());

        AuthException exception = assertThrows(AuthException.class, () ->
            authValidator.validateTokenValid("notValidToken"));

        assertEquals("Token is invalid.", exception.getMessage());
    }

    @Test
    void validateTokenValid_whenTokenExpired_throwsAuthException() {
        validToken.setExpiresAt(LocalDateTime.now().minusHours(1));
        when(tokenRepository.findByTokenHash("expiredToken")).thenReturn(Optional.of(validToken));

        AuthException exception = assertThrows(AuthException.class, () ->
            authValidator.validateTokenValid("expiredToken"));

        assertEquals("Token is expired.", exception.getMessage());
    }
}
