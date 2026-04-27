package com.example.minibankbackend.user.controller;

import com.example.minibankbackend.common.exception.GlobalExceptionHandler;
import com.example.minibankbackend.user.dto.UpdateUserProfileRequest;
import com.example.minibankbackend.user.dto.UserProfileResponse;
import com.example.minibankbackend.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(new UserController(userService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .setMessageConverters(new JacksonJsonHttpMessageConverter())
                .setValidator(validator)
                .build();
    }

    @Test
    void meShouldReadUserIdFromAuthentication() throws Exception {
        when(userService.getProfile(7L)).thenReturn(
                new UserProfileResponse(7L, "Student", "student@test.com", "USER", null)
        );

        mockMvc.perform(get("/api/users/me")
                        .principal(new UsernamePasswordAuthenticationToken("7", null)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.email").value("student@test.com"));

        verify(userService).getProfile(7L);
    }

    @Test
    void updateMeShouldPassRequestToService() throws Exception {
        when(userService.updateProfile(eq(9L), any(UpdateUserProfileRequest.class)))
                .thenReturn(new UserProfileResponse(9L, "New Name", "student@test.com", "USER", "https://cdn/a.png"));

        mockMvc.perform(patch("/api/users/me")
                        .principal(new UsernamePasswordAuthenticationToken("9", null))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "New Name",
                                  "avatarUrl": "https://cdn/a.png"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Name"))
                .andExpect(jsonPath("$.avatarUrl").value("https://cdn/a.png"));
    }

    @Test
    void updateMeShouldValidateBody() throws Exception {
        mockMvc.perform(patch("/api/users/me")
                        .principal(new UsernamePasswordAuthenticationToken("9", null))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "%s"
                                }
                                """.formatted("a".repeat(101))))
                .andExpect(status().isBadRequest());
    }
}
