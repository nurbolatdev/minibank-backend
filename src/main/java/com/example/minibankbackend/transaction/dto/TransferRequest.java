package com.example.minibankbackend.transaction.dto;


import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record TransferRequest(
        @NotNull Long fromAccountId,
        @NotNull Long toAccountId,
        @NotNull @DecimalMin(value="0.01") BigDecimal amount,
        @Size(max=200) String description
) {}
