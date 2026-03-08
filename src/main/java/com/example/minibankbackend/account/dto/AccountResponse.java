package com.example.minibankbackend.account.dto;



import com.example.minibankbackend.account.model.AccountCurrency;
import java.math.BigDecimal;

public record AccountResponse(Long id, AccountCurrency currency, BigDecimal balance) {}
