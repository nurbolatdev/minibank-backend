package com.example.minibankbackend.transaction.dto;



import com.example.minibankbackend.transaction.model.*;
import java.math.BigDecimal;
import java.time.Instant;

public record TransactionResponse(
        Long id,
        Long accountId,
        TxType type,
        TxStatus status,
        BigDecimal amount,
        String description,
        Instant createdAt
) {}
