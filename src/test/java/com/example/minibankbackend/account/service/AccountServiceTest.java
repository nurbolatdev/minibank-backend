package com.example.minibankbackend.account.service;

import com.example.minibankbackend.account.dto.CreateAccountRequest;
import com.example.minibankbackend.account.model.Account;
import com.example.minibankbackend.account.model.AccountCurrency;
import com.example.minibankbackend.account.repository.AccountRepository;
import com.example.minibankbackend.common.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    @Test
    void createShouldSaveAccountWithZeroBalance() {
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> {
            Account account = invocation.getArgument(0);
            account.setId(15L);
            return account;
        });

        var response = accountService.create(4L, new CreateAccountRequest(AccountCurrency.USD));

        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository).save(accountCaptor.capture());

        assertEquals(4L, accountCaptor.getValue().getUserId());
        assertEquals(AccountCurrency.USD, accountCaptor.getValue().getCurrency());
        assertEquals(BigDecimal.ZERO, accountCaptor.getValue().getBalance());
        assertEquals(15L, response.id());
        assertEquals(AccountCurrency.USD, response.currency());
        assertEquals(BigDecimal.ZERO, response.balance());
    }

    @Test
    void myAccountsShouldMapPageToResponse() {
        Account first = Account.builder().id(3L).userId(4L).currency(AccountCurrency.KZT).balance(new BigDecimal("100.00")).build();
        Account second = Account.builder().id(2L).userId(4L).currency(AccountCurrency.USD).balance(new BigDecimal("50.00")).build();

        when(accountRepository.findByUserId(any(), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(first, second), PageRequest.of(0, 10), 2));

        var response = accountService.myAccounts(4L, 0, 10);

        assertEquals(2, response.items().size());
        assertEquals(3L, response.items().getFirst().id());
        assertEquals(AccountCurrency.KZT, response.items().getFirst().currency());
        assertEquals(0, response.page());
        assertEquals(10, response.size());
        assertEquals(2, response.totalItems());
    }

    @Test
    void getOwnedShouldReturnAccountWhenItBelongsToUser() {
        Account account = Account.builder().id(9L).userId(4L).currency(AccountCurrency.USD).balance(BigDecimal.ONE).build();
        when(accountRepository.findByIdAndUserId(9L, 4L)).thenReturn(Optional.of(account));

        var result = accountService.getOwned(4L, 9L);

        assertEquals(9L, result.getId());
    }

    @Test
    void getOwnedShouldThrowWhenAccountMissing() {
        when(accountRepository.findByIdAndUserId(9L, 4L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> accountService.getOwned(4L, 9L));
    }

    @Test
    void getByIdShouldThrowWhenAccountMissing() {
        when(accountRepository.findById(12L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> accountService.getById(12L));
    }
}
