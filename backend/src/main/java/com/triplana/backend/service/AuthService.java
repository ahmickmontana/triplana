package com.triplana.backend.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.triplana.backend.dto.request.ForgotPasswordRequest;
import com.triplana.backend.dto.request.LoginRequest;
import com.triplana.backend.dto.request.RegisterRequest;
import com.triplana.backend.dto.request.ResendVerificationRequest;
import com.triplana.backend.dto.request.ResetPasswordRequest;
import com.triplana.backend.dto.response.LoginResponse;
import com.triplana.backend.dto.response.UserResponse;
import com.triplana.backend.entity.Token;
import com.triplana.backend.entity.TokenType;
import com.triplana.backend.entity.User;
import com.triplana.backend.exception.AuthException;
import com.triplana.backend.repository.TokenRepository;
import com.triplana.backend.repository.UserRepository;
import com.triplana.backend.validation.AuthValidator;

import jakarta.servlet.http.HttpSession;

import com.triplana.backend.util.TokenUtil;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final AuthValidator authValidator;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final TokenUtil tokenUtil;

    public void register(RegisterRequest request) {
        // This validates that the password and confirm password fields match.
        authValidator.validatePasswordsMatch(request.getPassword(), request.getConfirmPassword());

        // This validates the email entered in the email field has not been taken by another account.
        authValidator.validateEmailNotTaken(request.getEmail());

        // Hashing the password.
        String passwordHashed = passwordEncoder.encode(request.getPassword());

        User user = User.builder()
            .username(request.getUsername())
            .email(request.getEmail())
            .passwordHash(passwordHashed)
            .build();

        userRepository.save(user);

        String rawToken = tokenUtil.generateToken();
        String hashedToken = tokenUtil.hashToken(rawToken);

        Token token = Token.builder()
            .user(user)
            .tokenHash(hashedToken)
            .type(TokenType.VERIFICATION)
            .expiresAt(LocalDateTime.now().plusHours(24))
            .used(false)
            .build();

        tokenRepository.save(token);

        emailService.sendVerificationEmail(request.getEmail(), rawToken);
    }

    public void verifyEmail(String rawToken) {
        String hashedToken = tokenUtil.hashToken(rawToken);

        Token token = authValidator.validateTokenValid(hashedToken);
        User user = token.getUser();

        if (token.isUsed() || user.isVerified()) { 
            throw new AuthException("Account is already verified."); 
        }

        token.setUsed(true);
        user.setVerified(true);

        tokenRepository.save(token);
        userRepository.save(user);
    }

    public void resendVerification(ResendVerificationRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new AuthException("email", "An account does not exist for this email."));
        
        if (user.isVerified()) {
            throw new AuthException("email", "This account is already verified.");
        }

        tokenRepository.deleteAllByUserAndType(user, TokenType.VERIFICATION);

        String rawToken = tokenUtil.generateToken();
        String hashedToken = tokenUtil.hashToken(rawToken);

        Token token = Token.builder()
            .user(user)
            .tokenHash(hashedToken)
            .type(TokenType.VERIFICATION)
            .expiresAt(LocalDateTime.now().plusHours(24))
            .used(false)
            .build();

        tokenRepository.save(token);

        emailService.sendVerificationEmail(request.getEmail(), rawToken);
    }


    public LoginResponse login(LoginRequest request, HttpSession session) {
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new AuthException("Email or password is invalid."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new AuthException("Email or password is invalid.");
        }

        if (!user.isVerified()) {
            Optional<Token> existingToken = tokenRepository.findByUserAndType(user, TokenType.VERIFICATION);

            boolean hasValidToken = false;

            if (existingToken.isPresent()) {
                Token token = existingToken.get();
                hasValidToken = !token.isUsed() && !token.getExpiresAt().isBefore(LocalDateTime.now());
            }

            if (!hasValidToken) {
                tokenRepository.deleteAllByUserAndType(user, TokenType.VERIFICATION);

                String rawToken = tokenUtil.generateToken();
                String hashedToken = tokenUtil.hashToken(rawToken);

                Token token = Token.builder()
                    .user(user)
                    .tokenHash(hashedToken)
                    .type(TokenType.VERIFICATION)
                    .expiresAt(LocalDateTime.now().plusHours(24))
                    .used(false)
                    .build();

                tokenRepository.save(token);
                emailService.sendVerificationEmail(user.getEmail(), rawToken);
            }

            return LoginResponse.unverified();
        }

        session.setAttribute("userId", user.getId());
        return LoginResponse.success(user);
    }

    public void logout(HttpSession session) {
        session.invalidate();
    }

    /**
     * Sends a password reset email to the given email address.
     * Silently does nothing if the email is not registered — prevents email enumeration.
     *
     * @param request the forgot password request containing the email
     */
    public void forgotPassword(ForgotPasswordRequest request) {
        Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());

        if (optionalUser.isEmpty()) {
            return;
        }

        User user = optionalUser.get();

        tokenRepository.deleteAllByUserAndType(user, TokenType.PASSWORD_RESET);

        String rawToken = tokenUtil.generateToken();
        String hashedToken = tokenUtil.hashToken(rawToken);

        Token token = Token.builder()
            .user(user)
            .tokenHash(hashedToken)
            .type(TokenType.PASSWORD_RESET)
            .expiresAt(LocalDateTime.now().plusHours(1))
            .used(false)
            .build();

        tokenRepository.save(token);

        emailService.sendPasswordResetEmail(user.getEmail(), rawToken);
    }

    /**
     * Resets the user's password using a valid password reset token.
     *
     * @param request the reset password request containing the token and new password
     * @throws AuthException if passwords don't match, token is invalid, expired, or already used
     */
    public void resetPassword(ResetPasswordRequest request) {
        authValidator.validatePasswordsMatch(request.getNewPassword(), request.getConfirmPassword());

        String hashedToken = tokenUtil.hashToken(request.getToken());

        Token token = authValidator.validateTokenValid(hashedToken);

        if (token.isUsed()) {
            throw new AuthException("Token is invalid or expired.");
        }

        User user = token.getUser();

        String hashedPassword = passwordEncoder.encode(request.getNewPassword());
        user.setPasswordHash(hashedPassword);

        token.setUsed(true);

        userRepository.save(user);
        tokenRepository.save(token);
    }
}
