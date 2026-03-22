package com.example.minibankbackend.user.dto;



public record UserProfileResponse(
        Long id,
        String name,
        String email,
        String role,
        String avatarUrl
) {
}
