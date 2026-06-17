package com.triplana.backend.validation;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.triplana.backend.entity.Token;
import com.triplana.backend.exception.AuthException;
import com.triplana.backend.repository.TokenRepository;
import com.triplana.backend.repository.UserRepository;


@Component
public class AuthValidator {
    
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;

    public AuthValidator(UserRepository userRepository, TokenRepository tokenRepository) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
    }

    public void validatePasswordsMatch(String password, String confirmPassword) {
        if (!password.equals(confirmPassword)) {
            throw new AuthException("confirmPassword", "Passwords don't match.");
        };
    }

    public void validateEmailNotTaken(String email, String field) {
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new AuthException(field, "An account already exists under this email.");
        }
    }

    public Token validateTokenValid(String tokenHash) {
        Token token = tokenRepository.findByTokenHash(tokenHash)
            .orElseThrow(() -> new AuthException("This verification link is invalid."));

        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new AuthException("Your verification link is expired.");
        }

        return token;
    }
}
