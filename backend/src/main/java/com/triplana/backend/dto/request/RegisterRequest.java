package com.triplana.backend.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class RegisterRequest {
    
    @NotBlank(message = "Username cannot be empty")
    private String username;


    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Email must be in the format example@gmail.com")
    private String email;


    @NotBlank(message = "Password cannot be empty")
    @Pattern(
        regexp = "^(?=.*[A-Z])(?=.*\\\\d)(?=.*[^A-Za-z\\\\d]).{8,}$",
        message = "Password must be at least 8 characters long and contain at least one uppercase letter, one digit, and one special character"
    )
    private String password;

    @NotBlank(message = "Please confirm your password")
    private String confirmPassword;
}
