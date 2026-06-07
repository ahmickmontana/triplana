package com.triplana.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.triplana.backend.dto.request.ForgotPasswordRequest;
import com.triplana.backend.dto.request.LoginRequest;
import com.triplana.backend.dto.request.RegisterRequest;
import com.triplana.backend.dto.request.ResendVerificationRequest;
import com.triplana.backend.dto.request.ResetPasswordRequest;
import com.triplana.backend.dto.response.ApiResponse;
import com.triplana.backend.dto.response.LoginResponse;
import com.triplana.backend.dto.response.UserResponse;
import com.triplana.backend.entity.User;
import com.triplana.backend.exception.AuthException;
import com.triplana.backend.repository.UserRepository;
import com.triplana.backend.service.AuthService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;

    private final UserRepository userRepository;


    /**
     * Registers a new user account and sends a verification email
     * 
     * @param request the registration request containing username, email and password
     * @return 201 Created with success message
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(new ApiResponse(true, "Registration successful. Please check your email to verify your account."));
    }


    /**
     * Verifies a user's email address using the token from the verification link
     * 
     * @param token the verification token from the user's email link
     * @return 200 OK with success message
     */
    @GetMapping("/verify")
    public ResponseEntity<ApiResponse> verify(@RequestParam String token) {
        authService.verifyEmail(token);
        return ResponseEntity.ok(new ApiResponse(true, "Account verified successfully."));
    
    }


    /**
     * Resends a new verification email to the user's email
     * 
     * @param request the resend verification request containing the user's email
     * @return 200 OK with success message
     */
    @PostMapping("/resend-verification")
    public ResponseEntity<ApiResponse> resendVerification(@Valid @RequestBody ResendVerificationRequest request) {
        authService.resendVerification(request);
        return ResponseEntity.ok(new ApiResponse(true, "Verification email has been resent."));
    }


    /**
     * Authenticates a user and creates a session.
     * Returns unverified status if the account has not been verified yet.
     * 
     * @param request the login request containing email and password
     * @param session the current HTTP session
     * @return 200 OK with LoginResponse indicating success or unverified status.
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request, HttpSession session) {
        LoginResponse response = authService.login(request, session);
        return ResponseEntity.ok(response);
    }
    

    /**
     * Logs out the current user and invalidates their session
     * 
     * @param session the current HTTP session to invalidate
     * @return 200 OK with success message
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout(HttpSession session) {
        authService.logout(session);
        return ResponseEntity.ok(new ApiResponse(true, "Logged out successfully."));
    }
    

    /**
     * Sends a password reset email to the given email address.
     * Always returns a generic success message regardless of whether the email exists 
     * to prevent email enumeration attacks
     * 
     * @param request the forgot password request containing the email
     * @return 200 OK with generic success message
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request);
        return ResponseEntity.ok(new ApiResponse(true, "Password reset email has been sent. Please check your email to reset your password."));
    }


    /**
     * Resets the user's password using a valid password reset token
     * 
     * @param request the reset password request containing the token and new password
     * @return 200 OK with success message
     */
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok(new ApiResponse(true, "Password has been successfully reset. Please log in with your new password."));
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new AuthException("User not found."));
        return ResponseEntity.ok(UserResponse.from(user));
    }
}
