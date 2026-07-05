package com.triplana.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.triplana.backend.dto.request.UpdateProfileRequest;
import com.triplana.backend.dto.response.UserResponse;
import com.triplana.backend.entity.User;
import com.triplana.backend.exception.AuthException;
import com.triplana.backend.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    
    @Mock
private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = User.builder()
            .id(1L)
            .username("Ahmick")
            .email("ahmick@email.com")
            .passwordHash("hashedPassword")
            .verified(true)
            .build();
    }

    // updateProfile()

    @Test
    public void updateProfile_whenNewUsernameValidAndUserExists_userIsUpdatedSuccessfully() {
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(mockUser));

        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setUsername("Valid username");

        UserResponse response = userService.updateProfile(1L, request);

        assertEquals("Valid username", response.getUsername());
        verify(userRepository).save(any(User.class));
    }

    @Test
    public void updateProfile_userNotFound_throwsAuthException() {
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setUsername("Valid username");

        assertThrows(AuthException.class, () -> userService.updateProfile(1L, request));
    }
}
