package com.example.minibankbackend.account.controller;

import com.example.minibankbackend.account.dto.AccountResponse;
import com.example.minibankbackend.account.model.AccountCurrency;
import com.example.minibankbackend.account.service.AccountService;
import com.example.minibankbackend.common.exception.GlobalExceptionHandler;
import com.example.minibankbackend.common.response.PageResponse;
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

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AccountControllerTest {

    @Mock
    private AccountService accountService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(new AccountController(accountService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .setMessageConverters(new JacksonJsonHttpMessageConverter())
                .setValidator(validator)
                .build();
    }

    @Test
    void createShouldReadUserIdFromAuthentication() throws Exception {
        when(accountService.create(eq(5L), any()))
                .thenReturn(new AccountResponse(11L, AccountCurrency.KZT, BigDecimal.ZERO));

        mockMvc.perform(post("/api/accounts")
                        .principal(new UsernamePasswordAuthenticationToken("5", null))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "currency": "KZT"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(11))
                .andExpect(jsonPath("$.currency").value("KZT"));

        verify(accountService).create(eq(5L), any());
    }

    @Test
    void createShouldValidateRequest() throws Exception {
        mockMvc.perform(post("/api/accounts")
                        .principal(new UsernamePasswordAuthenticationToken("5", null))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void myShouldReturnAccountsPage() throws Exception {
        when(accountService.myAccounts(5L, 0, 10)).thenReturn(
                new PageResponse<>(
                        List.of(new AccountResponse(11L, AccountCurrency.USD, new BigDecimal("45.00"))),
                        0,
                        10,
                        1,
                        1
                )
        );

        mockMvc.perform(get("/api/accounts")
                        .principal(new UsernamePasswordAuthenticationToken("5", null))
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].id").value(11))
                .andExpect(jsonPath("$.items[0].balance").value(45.00))
                .andExpect(jsonPath("$.totalItems").value(1));
    }
}
