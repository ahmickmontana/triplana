package com.triplana.backend.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ForgotPasswordRequest {
    
    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Email must be in format example@email.com")
    private String email;
}
