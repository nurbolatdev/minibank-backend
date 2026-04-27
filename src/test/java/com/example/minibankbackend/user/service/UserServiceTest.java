package com.example.minibankbackend.user.service;

import com.example.minibankbackend.common.exception.NotFoundException;
import com.example.minibankbackend.user.dto.UpdateUserProfileRequest;
import com.example.minibankbackend.user.model.Role;
import com.example.minibankbackend.user.model.User;
import com.example.minibankbackend.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void meShouldReturnBasicUserInfo() {
        User user = User.builder()
                .id(3L)
                .email("me@test.com")
                .role(Role.USER)
                .build();
        when(userRepository.findById(3L)).thenReturn(Optional.of(user));

        var response = userService.me(3L);

        assertEquals(3L, response.id());
        assertEquals("me@test.com", response.email());
        assertEquals("USER", response.role());
    }

    @Test
    void getProfileShouldReturnFullProfile() {
        User user = User.builder()
                .id(10L)
                .name("Nurbek")
                .email("nurbek@test.com")
                .role(Role.USER)
                .avatarUrl("https://cdn/avatar.png")
                .build();
        when(userRepository.findById(10L)).thenReturn(Optional.of(user));

        var response = userService.getProfile(10L);

        assertEquals("Nurbek", response.name());
        assertEquals("nurbek@test.com", response.email());
        assertEquals("USER", response.role());
        assertEquals("https://cdn/avatar.png", response.avatarUrl());
    }

    @Test
    void updateProfileShouldTrimValuesAndSaveUser() {
        User user = User.builder()
                .id(11L)
                .name("Old")
                .email("user@test.com")
                .role(Role.USER)
                .avatarUrl("old")
                .build();
        when(userRepository.findById(11L)).thenReturn(Optional.of(user));

        var response = userService.updateProfile(
                11L,
                new UpdateUserProfileRequest("  New Name  ", "  https://cdn/new.png  ")
        );

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        assertEquals("New Name", userCaptor.getValue().getName());
        assertEquals("https://cdn/new.png", userCaptor.getValue().getAvatarUrl());
        assertEquals("New Name", response.name());
        assertEquals("https://cdn/new.png", response.avatarUrl());
    }

    @Test
    void getProfileShouldThrowWhenUserMissing() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getProfile(99L));
    }
}
