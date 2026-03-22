package com.example.minibankbackend.user.service;



import com.example.minibankbackend.common.exception.NotFoundException;
import com.example.minibankbackend.user.dto.MeResponse;
import com.example.minibankbackend.user.dto.UpdateUserProfileRequest;
import com.example.minibankbackend.user.dto.UserProfileResponse;
import com.example.minibankbackend.user.model.User;
import com.example.minibankbackend.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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


    public UserProfileResponse getProfile(Long userId) {
        User user = repo.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        return new UserProfileResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().name(),
                user.getAvatarUrl()
        );
    }

    @Transactional
    public UserProfileResponse updateProfile(Long userId, UpdateUserProfileRequest req) {
        User user = repo.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (req.name() != null) {
            user.setName(req.name().trim());
        }

        if (req.avatarUrl() != null) {
            user.setAvatarUrl(req.avatarUrl().trim());
        }

        repo.save(user);

        return new UserProfileResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().name(),
                user.getAvatarUrl()
        );
    }
}