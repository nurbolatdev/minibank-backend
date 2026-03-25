package com.example.minibankbackend.user.controller;

import com.example.minibankbackend.user.dto.UpdateUserProfileRequest;
import com.example.minibankbackend.user.dto.UserProfileResponse;
import com.example.minibankbackend.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping("/me")
    public UserProfileResponse me(Authentication auth) {
        Long userId = Long.valueOf(auth.getName());
        return service.getProfile(userId);
    }

    @PatchMapping("/me")
    public UserProfileResponse updateMe(
            Authentication auth,
            @Valid @RequestBody UpdateUserProfileRequest req
    ) {
        Long userId = Long.valueOf(auth.getName());
        return service.updateProfile(userId, req);
    }
}
