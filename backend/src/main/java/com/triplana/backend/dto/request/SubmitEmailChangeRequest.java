package com.triplana.backend.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class SubmitEmailChangeRequest {
    
    @NotBlank
    private String token;

    @NotBlank(message = "Email cannot be empty")
    @Pattern(
        regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
        message = "Email must be in format example@email.com"
    )
    private String newEmail;

    @NotBlank(message = "Password cannot be empty")
    private String password;
    
}
