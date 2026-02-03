package com.example.accounting.dto;

// imports for validation annotations
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
// lombok annotation to generate boilerplate code
import lombok.Data;

// Data Transfer Object for user registration requests
@Data
public class RegisterRequest {
    // Validation annotations for username, email, and password fields
    @NotBlank
    @Size(min = 3, max = 100)
    private String username;
    
    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 6, max = 100)
    private String password;
}
