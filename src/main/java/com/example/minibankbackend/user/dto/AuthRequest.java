package com.example.minibankbackend.user.dto;



import jakarta.validation.constraints.*;

public record AuthRequest(
        @Email @NotBlank String email,
        @NotBlank String password
) {}
