package com.triplana.backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.triplana.backend.dto.request.ForgotPasswordRequest;
import com.triplana.backend.dto.request.LoginRequest;
import com.triplana.backend.dto.request.RegisterRequest;
import com.triplana.backend.dto.request.ResendVerificationRequest;
import com.triplana.backend.dto.request.ResetPasswordRequest;
import com.triplana.backend.dto.response.LoginResponse;
import com.triplana.backend.entity.Token;
import com.triplana.backend.entity.TokenType;
import com.triplana.backend.entity.User;
import com.triplana.backend.exception.AuthException;
import com.triplana.backend.repository.TokenRepository;
import com.triplana.backend.repository.UserRepository;
import com.triplana.backend.util.TokenUtil;
import com.triplana.backend.validation.AuthValidator;

import jakarta.servlet.http.HttpSession;


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

    @Mock
    private HttpSession session;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest request;

    private User mockUser;

    private Token mockToken;
    

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

        mockToken = Token.builder()
            .tokenHash("hashedToken")
            .type(TokenType.VERIFICATION)
            .expiresAt(LocalDateTime.now().plusHours(24))
            .used(false)
            .user(mockUser)
            .build();
    }


    // register()

    @Test
    void register_whenAllFieldsValid_registersUserSuccessfully() {
        when(tokenUtil.generateToken()).thenReturn("rawToken");
        when(tokenUtil.hashToken("rawToken")).thenReturn("hashedToken");
        assertDoesNotThrow(() -> authService.register(request));
        
        verify(userRepository).save(any(User.class));
        verify(tokenRepository).save(any(Token.class));
        verify(emailService).sendVerificationEmail(eq("valid@email.com"), anyString());
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


    // verifyEmail()

    @Test
    void verifyEmail_whenTokenValidAndUserNotVerified_verifiesUser() {
        when(tokenUtil.hashToken(anyString())).thenReturn("hashedToken");
        when(authValidator.validateTokenValid("hashedToken")).thenReturn(mockToken);

        assertDoesNotThrow(() -> authService.verifyEmail("rawToken"));

        verify(tokenRepository).save(mockToken);
        verify(userRepository).save(mockUser);
        assertTrue(mockToken.isUsed());
        assertTrue(mockUser.isVerified());
    }

    @Test
    void verifyEmail_whenTokenAlreadyUsed_throwsAuthException() {
        mockToken.setUsed(true);

        when(tokenUtil.hashToken(anyString())).thenReturn("hashedToken");
        when(authValidator.validateTokenValid("hashedToken")).thenReturn(mockToken);

        assertThrows(AuthException.class, () -> authService.verifyEmail("rawToken"));
    }

    @Test
    void verifyEmail_whenUserAlreadyVerified_throwsAuthException() {
        mockUser.setVerified(true);

        when(tokenUtil.hashToken(anyString())).thenReturn("hashedToken");
        when(authValidator.validateTokenValid("hashedToken")).thenReturn(mockToken);

        assertThrows(AuthException.class, () -> authService.verifyEmail("rawToken"));
    }

    @Test
    void verifyEmail_whenTokenInvalidOrExpired_throwsAuthException() {
        when(tokenUtil.hashToken(anyString())).thenReturn("hashedToken");
        doThrow(new AuthException("Token is invalid or expired."))
            .when(authValidator).validateTokenValid("hashedToken");

        assertThrows(AuthException.class, () -> authService.verifyEmail("rawToken"));
    }


    // resendVerification()

    @Test
    void resendVerification_whenUserExistsAndNotVerified_verificationResent() {
        when(userRepository.findByEmail("valid@email.com")).thenReturn(Optional.of(mockUser));
        when(tokenUtil.generateToken()).thenReturn("rawToken");
        when(tokenUtil.hashToken("rawToken")).thenReturn("hashedToken");

        assertDoesNotThrow(() ->
            authService.resendVerification(new ResendVerificationRequest() {{
                setEmail("valid@email.com");
            }})
        );

        verify(tokenRepository).deleteAllByUserAndType(mockUser, TokenType.VERIFICATION);
        verify(tokenRepository).save(any(Token.class));
        verify(emailService).sendVerificationEmail(eq("valid@email.com"), eq("rawToken"));
    }

    @Test
    void resendVerification_whenUserNotFound_throwsAuthException() {
        when(userRepository.findByEmail("valid@email.com")).thenReturn(Optional.empty());

        assertThrows(AuthException.class, () ->
            authService.resendVerification(new ResendVerificationRequest() {{
                setEmail("valid@email.com");
            }})
        );
    }

    @Test
    void resendVerification_whenUserAlreadyVerified_throwsAuthException() {
        mockUser.setVerified(true);
        when(userRepository.findByEmail("valid@email.com")).thenReturn(Optional.of(mockUser));

        assertThrows(AuthException.class, () ->
            authService.resendVerification(new ResendVerificationRequest() {{
                setEmail("valid@email.com");
            }})
        );
    }


    // login()

    @Test
    void login_whenCredentialsValidAndVerified_returnsSuccessResponse() {
        mockUser.setVerified(true);

        when(userRepository.findByEmail("valid@email.com")).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("ValidPassword1!", "hashedPassword")).thenReturn(true);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("valid@email.com");
        loginRequest.setPassword("ValidPassword1!");

        LoginResponse response = authService.login(loginRequest, session);

        assertEquals("success", response.getStatus());
        assertNotNull(response.getUser());
        verify(session).setAttribute("userId", mockUser.getId());
    }

    @Test
    void login_whenUserNotFound_throwsAuthException() {
        when(userRepository.findByEmail("valid@email.com")).thenReturn(Optional.empty());

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("valid@email.com");
        loginRequest.setPassword("ValidPassword1!");

        assertThrows(AuthException.class, () ->
            authService.login(loginRequest, session));
    }

    @Test
    void login_whenPasswordIncorrect_throwsAuthException() {
        when(userRepository.findByEmail("valid@email.com")).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("ValidPassword1!", "hashedPassword")).thenReturn(false);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("valid@email.com");
        loginRequest.setPassword("ValidPassword1!");

        assertThrows(AuthException.class, () ->
            authService.login(loginRequest, session));
    }

    @Test
    void login_whenUserUnverifiedWithValidToken_returnsUnverifiedResponse() {
        when(userRepository.findByEmail("valid@email.com")).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("ValidPassword1!", "hashedPassword")).thenReturn(true);
        when(tokenRepository.findByUserAndType(mockUser, TokenType.VERIFICATION))
            .thenReturn(Optional.of(mockToken));

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("valid@email.com");
        loginRequest.setPassword("ValidPassword1!");

        LoginResponse response = authService.login(loginRequest, session);

        assertEquals("unverified", response.getStatus());
    }

    @Test
    void login_whenUserUnverifiedWithNoValidToken_sendsNewVerificationEmail() {
        when(userRepository.findByEmail("valid@email.com")).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("ValidPassword1!", "hashedPassword")).thenReturn(true);
        when(tokenRepository.findByUserAndType(mockUser, TokenType.VERIFICATION))
            .thenReturn(Optional.empty());
        when(tokenUtil.generateToken()).thenReturn("rawToken");
        when(tokenUtil.hashToken("rawToken")).thenReturn("hashedToken");

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("valid@email.com");
        loginRequest.setPassword("ValidPassword1!");

        LoginResponse response = authService.login(loginRequest, session);

        assertEquals("unverified", response.getStatus());
        verify(emailService).sendVerificationEmail(eq("valid@email.com"), eq("rawToken"));
    }

    
    // logOut()

    @Test
    void logout_whenCalled_invalidatesSession() {
        authService.logout(session);
        verify(session).invalidate();
    }


    // forgotPassword()
    @Test
    void forgotPassword_whenEmailNotFound_doesNotSendEmail() {
        when(userRepository.findByEmail("valid@email.com")).thenReturn(Optional.empty());

        ForgotPasswordRequest forgotRequest = new ForgotPasswordRequest();
        forgotRequest.setEmail("valid@email.com");

        assertDoesNotThrow(() -> authService.forgotPassword(forgotRequest));

        verify(emailService, never()).sendPasswordResetEmail(anyString(), anyString());
    }

    @Test
    void forgotPassword_whenEmailExists_sendsResetEmail() {
        when(userRepository.findByEmail("valid@email.com")).thenReturn(Optional.of(mockUser));
        when(tokenUtil.generateToken()).thenReturn("rawToken");
        when(tokenUtil.hashToken("rawToken")).thenReturn("hashedToken");

        ForgotPasswordRequest forgotRequest = new ForgotPasswordRequest();
        forgotRequest.setEmail("valid@email.com");

        assertDoesNotThrow(() -> authService.forgotPassword(forgotRequest));

        verify(tokenRepository).deleteAllByUserAndType(mockUser, TokenType.PASSWORD_RESET);
        verify(tokenRepository).save(any(Token.class));
        verify(emailService).sendPasswordResetEmail(eq("valid@email.com"), eq("rawToken"));
    }

    // resetPassword()

    @Test
    void resetPassword_whenPasswordsDoNotMatch_throwsAuthException() {
        ResetPasswordRequest resetRequest = new ResetPasswordRequest();
        resetRequest.setToken("rawToken");
        resetRequest.setNewPassword("ValidPassword1!");
        resetRequest.setConfirmPassword("DifferentPassword1!");

        doThrow(new AuthException("confirmPassword", "Passwords don't match."))
            .when(authValidator).validatePasswordsMatch("ValidPassword1!", "DifferentPassword1!");

        assertThrows(AuthException.class, () -> authService.resetPassword(resetRequest));
    }

    @Test
    void resetPassword_whenTokenInvalidOrExpired_throwsAuthException() {
        ResetPasswordRequest resetRequest = new ResetPasswordRequest();
        resetRequest.setToken("rawToken");
        resetRequest.setNewPassword("ValidPassword1!");
        resetRequest.setConfirmPassword("ValidPassword1!");

        when(tokenUtil.hashToken("rawToken")).thenReturn("hashedToken");
        doThrow(new AuthException("Token is invalid or expired."))
            .when(authValidator).validateTokenValid("hashedToken");

        assertThrows(AuthException.class, () -> authService.resetPassword(resetRequest));
    }

    @Test
    void resetPassword_whenTokenAlreadyUsed_throwsAuthException() {
        mockToken.setUsed(true);

        ResetPasswordRequest resetRequest = new ResetPasswordRequest();
        resetRequest.setToken("rawToken");
        resetRequest.setNewPassword("ValidPassword1!");
        resetRequest.setConfirmPassword("ValidPassword1!");

        when(tokenUtil.hashToken("rawToken")).thenReturn("hashedToken");
        when(authValidator.validateTokenValid("hashedToken")).thenReturn(mockToken);

        assertThrows(AuthException.class, () -> authService.resetPassword(resetRequest));
    }

    @Test
    void resetPassword_whenAllValid_updatesPasswordAndMarksTokenUsed() {
        ResetPasswordRequest resetRequest = new ResetPasswordRequest();
        resetRequest.setToken("rawToken");
        resetRequest.setNewPassword("ValidPassword1!");
        resetRequest.setConfirmPassword("ValidPassword1!");

        when(tokenUtil.hashToken("rawToken")).thenReturn("hashedToken");
        when(authValidator.validateTokenValid("hashedToken")).thenReturn(mockToken);
        when(passwordEncoder.encode("ValidPassword1!")).thenReturn("newHashedPassword");

        assertDoesNotThrow(() -> authService.resetPassword(resetRequest));

        verify(userRepository).save(mockUser);
        verify(tokenRepository).save(mockToken);
        assertTrue(mockToken.isUsed());
        assertEquals("newHashedPassword", mockUser.getPasswordHash());
    }
}
