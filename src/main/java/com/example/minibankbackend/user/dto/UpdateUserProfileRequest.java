package com.example.minibankbackend.user.dto;



import jakarta.validation.constraints.Size;

public record UpdateUserProfileRequest(
        @Size(max = 100, message = "Name must be at most 100 characters")
        String name,

        @Size(max = 1000, message = "Avatar URL must be at most 1000 characters")
        String avatarUrl
) {
}
