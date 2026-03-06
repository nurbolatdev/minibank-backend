package com.example.minibankbackend.user.service;



import com.example.minibankbackend.common.exception.NotFoundException;
import com.example.minibankbackend.user.dto.MeResponse;
import com.example.minibankbackend.user.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository repo;

    public UserService(UserRepository repo) {
        this.repo = repo;
    }

    public MeResponse me(Long userId) {
        var u = repo.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        return new MeResponse(u.getId(), u.getEmail(), u.getRole().name());
    }
}