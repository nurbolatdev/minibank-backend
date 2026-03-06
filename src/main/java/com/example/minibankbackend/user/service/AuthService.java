package com.example.minibankbackend.user.service;


import com.example.minibankbackend.common.security.JwtService;
import com.example.minibankbackend.user.dto.AuthRequest;
import com.example.minibankbackend.user.dto.AuthResponse;
import com.example.minibankbackend.user.dto.RegisterRequest;
import com.example.minibankbackend.user.model.Role;
import com.example.minibankbackend.user.model.User;
import com.example.minibankbackend.user.repository.UserRepository;
import com.example.minibankbackend.common.exception.BusinessException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public AuthService(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    public void register(RegisterRequest req) {
        if (userRepository.findByEmail(req.email()).isPresent()) {
            throw new BusinessException("EMAIL_EXISTS", "Email already registered");
        }
        User user = User.builder()
                .email(req.email())
                .passwordHash(encoder.encode(req.password()))
                .role(Role.USER)
                .build();
        userRepository.save(user);
    }

    public AuthResponse login(AuthRequest req) {
        User user = userRepository.findByEmail(req.email())
                .orElseThrow(() -> new BusinessException("BAD_CREDENTIALS", "Invalid credentials"));

        if (!encoder.matches(req.password(), user.getPasswordHash())) {
            throw new BusinessException("BAD_CREDENTIALS", "Invalid credentials");
        }

        String token = jwtService.generate(user.getId().toString(), user.getEmail(), user.getRole().name());
        return new AuthResponse(token);
    }
}
