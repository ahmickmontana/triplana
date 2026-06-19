package com.triplana.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class RegisterRequest {
    
    @NotBlank(message = "Username cannot be empty.")
    private String username;


    @NotBlank(message = "Email cannot be empty.")
    @Pattern(
        regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
        message = "Email must be in format example@email.com"
    )
    private String email;


    @NotBlank(message = "Password cannot be empty.")
    @Pattern(
        regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,}$",
        message = "'Password must be 8 characters long, have at least one capital letter, a digit and a symbol."
    )
    private String password;

    @NotBlank(message = "Please confirm your password.")
    private String confirmPassword;
}
