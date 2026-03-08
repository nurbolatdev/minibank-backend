package com.example.minibankbackend.account.dto;



import com.example.minibankbackend.account.model.AccountCurrency;
import jakarta.validation.constraints.NotNull;

public record CreateAccountRequest(@NotNull AccountCurrency currency) {}