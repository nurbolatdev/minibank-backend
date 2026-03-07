package com.example.minibankbackend.user.controller;



import com.example.minibankbackend.user.dto.MeResponse;
import com.example.minibankbackend.user.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService service;

    public UserController(UserService service) { this.service = service; }

    @GetMapping("/me")
    public MeResponse me(Authentication auth) {
        Long userId = Long.valueOf(auth.getName());
        return service.me(userId);
    }
}