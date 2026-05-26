package com.triplana.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ResetPasswordRequest {

    @NotBlank
    private String token;

    @NotBlank(message = "Password cannot be empty")
    @Pattern(
        regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,}$",
        message = "Password must be 8 characters long, have at least one capital letter, a digit and a symbol"
    )
    private String newPassword;

    @NotBlank(message = "Please confirm your password")
    private String confirmPassword;
}