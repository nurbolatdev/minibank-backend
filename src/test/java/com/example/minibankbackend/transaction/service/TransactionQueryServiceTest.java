package com.example.minibankbackend.transaction.service;

import com.example.minibankbackend.transaction.model.Transaction;
import com.example.minibankbackend.transaction.model.TxStatus;
import com.example.minibankbackend.transaction.model.TxType;
import com.example.minibankbackend.transaction.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionQueryServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionQueryService transactionQueryService;

    @Test
    void listShouldUseDateRangeQueryWhenBothDatesArePresent() {
        Instant from = Instant.parse("2024-01-01T00:00:00Z");
        Instant to = Instant.parse("2024-01-31T00:00:00Z");
        Transaction transaction = Transaction.builder()
                .id(1L)
                .accountId(5L)
                .type(TxType.TRANSFER_IN)
                .status(TxStatus.SUCCESS)
                .amount(new BigDecimal("25.00"))
                .description("salary")
                .createdAt(from)
                .build();

        when(transactionRepository.findByAccountIdAndCreatedAtBetween(eq(5L), eq(from), eq(to), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(transaction), PageRequest.of(0, 10), 1));

        var response = transactionQueryService.list(5L, from, to, 0, 10);

        assertEquals(1, response.items().size());
        assertEquals("salary", response.items().getFirst().description());
        verify(transactionRepository).findByAccountIdAndCreatedAtBetween(eq(5L), eq(from), eq(to), any(PageRequest.class));
    }

    @Test
    void listShouldUseSimpleQueryWhenDatesAreMissing() {
        Transaction transaction = Transaction.builder()
                .id(2L)
                .accountId(5L)
                .type(TxType.TRANSFER_OUT)
                .status(TxStatus.SUCCESS)
                .amount(new BigDecimal("10.00"))
                .description("coffee")
                .createdAt(Instant.parse("2024-02-01T00:00:00Z"))
                .build();

        when(transactionRepository.findByAccountId(eq(5L), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(transaction), PageRequest.of(0, 10), 1));

        var response = transactionQueryService.list(5L, null, null, 0, 10);

        assertEquals(1, response.items().size());
        assertEquals(TxType.TRANSFER_OUT, response.items().getFirst().type());
        verify(transactionRepository).findByAccountId(eq(5L), any(PageRequest.class));
    }
}
