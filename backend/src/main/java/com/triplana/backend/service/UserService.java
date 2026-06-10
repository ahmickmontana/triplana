package com.triplana.backend.service;

import org.springframework.stereotype.Service;

import com.triplana.backend.dto.request.UpdateProfileRequest;
import com.triplana.backend.dto.response.UserResponse;
import com.triplana.backend.entity.User;
import com.triplana.backend.exception.AuthException;
import com.triplana.backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;


    public UserResponse updateProfile(Long userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new AuthException("User not found."));

        user.setUsername(request.getUsername());

        userRepository.save(user);

        return UserResponse.from(user);
    }
}
