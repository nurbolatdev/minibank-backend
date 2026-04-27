package com.example.minibankbackend.user.service;

import com.example.minibankbackend.common.exception.BusinessException;
import com.example.minibankbackend.common.security.JwtService;
import com.example.minibankbackend.user.dto.AuthRequest;
import com.example.minibankbackend.user.dto.RegisterRequest;
import com.example.minibankbackend.user.model.Role;
import com.example.minibankbackend.user.model.User;
import com.example.minibankbackend.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    private static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder();

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    void registerShouldNormalizeEmailAndSaveUser() {
        RegisterRequest request = new RegisterRequest("  Test@Email.com  ", "secret1");
        when(userRepository.findByEmail(request.email())).thenReturn(Optional.empty());

        authService.register(request);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        assertEquals("test@email.com", savedUser.getEmail());
        assertEquals(Role.USER, savedUser.getRole());
        assertEquals("test", savedUser.getName());
        assertNull(savedUser.getAvatarUrl());
        assertNotEquals("secret1", savedUser.getPasswordHash());
    }

    @Test
    void registerShouldThrowWhenEmailAlreadyExists() {
        RegisterRequest request = new RegisterRequest("test@email.com", "secret1");
        when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(User.builder().build()));

        BusinessException ex = assertThrows(BusinessException.class, () -> authService.register(request));

        assertEquals("EMAIL_EXISTS", ex.getCode());
        verify(userRepository, never()).save(any());
    }

    @Test
    void loginShouldReturnTokenWhenCredentialsAreValid() {
        User user = User.builder()
                .id(5L)
                .email("test@email.com")
                .passwordHash(ENCODER.encode("secret1"))
                .role(Role.USER)
                .build();
        AuthRequest request = new AuthRequest("  Test@Email.com ", "secret1");

        when(userRepository.findByEmail("test@email.com")).thenReturn(Optional.of(user));
        when(jwtService.generate("5", "test@email.com", "USER")).thenReturn("jwt-token");

        var response = authService.login(request);

        assertEquals("jwt-token", response.token());
        verify(jwtService).generate("5", "test@email.com", "USER");
    }

    @Test
    void loginShouldThrowWhenPasswordDoesNotMatch() {
        User user = User.builder()
                .id(5L)
                .email("test@email.com")
                .passwordHash(ENCODER.encode("secret1"))
                .role(Role.USER)
                .build();

        when(userRepository.findByEmail("test@email.com")).thenReturn(Optional.of(user));

        BusinessException ex = assertThrows(
                BusinessException.class,
                () -> authService.login(new AuthRequest("test@email.com", "wrong-pass"))
        );

        assertEquals("BAD_CREDENTIALS", ex.getCode());
        verify(jwtService, never()).generate(any(), any(), any());
    }
}
