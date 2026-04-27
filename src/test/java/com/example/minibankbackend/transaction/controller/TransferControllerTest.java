package com.example.minibankbackend.transaction.controller;

import com.example.minibankbackend.common.exception.GlobalExceptionHandler;
import com.example.minibankbackend.transaction.service.TransferService;
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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TransferControllerTest {

    @Mock
    private TransferService transferService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(new TransferController(transferService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .setMessageConverters(new JacksonJsonHttpMessageConverter())
                .setValidator(validator)
                .build();
    }

    @Test
    void transferShouldReadUserIdFromAuthentication() throws Exception {
        doNothing().when(transferService).transfer(eq(6L), any());

        mockMvc.perform(post("/api/transfers")
                        .principal(new UsernamePasswordAuthenticationToken("6", null))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "fromAccountId": 1,
                                  "toAccountId": 2,
                                  "amount": 15.50,
                                  "description": "small transfer"
                                }
                                """))
                .andExpect(status().isOk());

        verify(transferService).transfer(eq(6L), any());
    }

    @Test
    void transferShouldValidateBody() throws Exception {
        mockMvc.perform(post("/api/transfers")
                        .principal(new UsernamePasswordAuthenticationToken("6", null))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "fromAccountId": 1,
                                  "toAccountId": 2,
                                  "amount": 0
                                }
                                """))
                .andExpect(status().isBadRequest());
    }
}
