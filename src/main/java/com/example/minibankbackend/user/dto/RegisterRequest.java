package com.example.minibankbackend.user.dto;



import jakarta.validation.constraints.*;

public record RegisterRequest(
        @Email @NotBlank String email,
        @Size(min=6, max=100) String password
) {}