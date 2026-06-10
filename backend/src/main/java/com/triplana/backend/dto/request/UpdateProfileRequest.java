package com.triplana.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateProfileRequest {
    
    @NotBlank(message = "Username cannot be empty")
    private String username;
}
