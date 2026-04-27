package com.example.minibankbackend.transaction.controller;

import com.example.minibankbackend.common.exception.GlobalExceptionHandler;
import com.example.minibankbackend.common.response.PageResponse;
import com.example.minibankbackend.transaction.dto.TransactionResponse;
import com.example.minibankbackend.transaction.model.TxStatus;
import com.example.minibankbackend.transaction.model.TxType;
import com.example.minibankbackend.transaction.service.TransactionQueryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TransactionControllerTest {

    @Mock
    private TransactionQueryService transactionQueryService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new TransactionController(transactionQueryService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .setMessageConverters(new JacksonJsonHttpMessageConverter())
                .build();
    }

    @Test
    void listShouldReturnTransactionsPage() throws Exception {
        Instant createdAt = Instant.parse("2024-03-10T10:15:30Z");
        when(transactionQueryService.list(8L, null, null, 0, 10)).thenReturn(
                new PageResponse<>(
                        List.of(new TransactionResponse(1L, 8L, TxType.TRANSFER_IN, TxStatus.SUCCESS, new BigDecimal("150.00"), "salary", createdAt)),
                        0,
                        10,
                        1,
                        1
                )
        );

        mockMvc.perform(get("/api/transactions")
                        .param("accountId", "8")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].accountId").value(8))
                .andExpect(jsonPath("$.items[0].type").value("TRANSFER_IN"))
                .andExpect(jsonPath("$.items[0].description").value("salary"));

        verify(transactionQueryService).list(8L, null, null, 0, 10);
    }

    @Test
    void listShouldPassDateFiltersToService() throws Exception {
        Instant from = Instant.parse("2024-03-01T00:00:00Z");
        Instant to = Instant.parse("2024-03-31T23:59:59Z");
        when(transactionQueryService.list(8L, from, to, 1, 5)).thenReturn(
                new PageResponse<>(List.of(), 1, 5, 0, 0)
        );

        mockMvc.perform(get("/api/transactions")
                        .param("accountId", "8")
                        .param("from", from.toString())
                        .param("to", to.toString())
                        .param("page", "1")
                        .param("size", "5"))
                .andExpect(status().isOk());

        verify(transactionQueryService).list(8L, from, to, 1, 5);
    }
}
