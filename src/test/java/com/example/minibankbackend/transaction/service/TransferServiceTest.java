package com.example.minibankbackend.transaction.service;

import com.example.minibankbackend.account.model.Account;
import com.example.minibankbackend.account.model.AccountCurrency;
import com.example.minibankbackend.account.service.AccountService;
import com.example.minibankbackend.common.exception.BusinessException;
import com.example.minibankbackend.transaction.dto.TransferRequest;
import com.example.minibankbackend.transaction.model.Transaction;
import com.example.minibankbackend.transaction.model.TxStatus;
import com.example.minibankbackend.transaction.model.TxType;
import com.example.minibankbackend.transaction.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransferServiceTest {

    @Mock
    private AccountService accountService;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransferService transferService;

    @Test
    void transferShouldMoveBalanceAndCreateTwoTransactions() {
        Account from = Account.builder()
                .id(1L)
                .userId(7L)
                .currency(AccountCurrency.KZT)
                .balance(new BigDecimal("1000.00"))
                .build();
        Account to = Account.builder()
                .id(2L)
                .userId(9L)
                .currency(AccountCurrency.KZT)
                .balance(new BigDecimal("250.00"))
                .build();
        TransferRequest request = new TransferRequest(1L, 2L, new BigDecimal("150.00"), "for test");

        when(accountService.getOwned(7L, 1L)).thenReturn(from);
        when(accountService.getById(2L)).thenReturn(to);

        transferService.transfer(7L, request);

        assertEquals(new BigDecimal("850.00"), from.getBalance());
        assertEquals(new BigDecimal("400.00"), to.getBalance());
        verify(accountService).save(from);
        verify(accountService).save(to);

        ArgumentCaptor<Transaction> txCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository, times(2)).save(txCaptor.capture());
        List<Transaction> savedTransactions = txCaptor.getAllValues();

        assertEquals(TxType.TRANSFER_OUT, savedTransactions.get(0).getType());
        assertEquals(TxType.TRANSFER_IN, savedTransactions.get(1).getType());
        assertEquals(TxStatus.SUCCESS, savedTransactions.get(0).getStatus());
        assertEquals(new BigDecimal("150.00"), savedTransactions.get(0).getAmount());
        assertEquals("for test", savedTransactions.get(0).getDescription());
    }

    @Test
    void transferShouldThrowWhenAccountsAreSame() {
        BusinessException ex = assertThrows(
                BusinessException.class,
                () -> transferService.transfer(7L, new TransferRequest(1L, 1L, new BigDecimal("10.00"), null))
        );

        assertEquals("SAME_ACCOUNT", ex.getCode());
        verifyNoInteractions(accountService, transactionRepository);
    }

    @Test
    void transferShouldThrowWhenCurrenciesDoNotMatch() {
        Account from = Account.builder().id(1L).userId(7L).currency(AccountCurrency.KZT).balance(new BigDecimal("1000.00")).build();
        Account to = Account.builder().id(2L).userId(9L).currency(AccountCurrency.USD).balance(new BigDecimal("250.00")).build();

        when(accountService.getOwned(7L, 1L)).thenReturn(from);
        when(accountService.getById(2L)).thenReturn(to);

        BusinessException ex = assertThrows(
                BusinessException.class,
                () -> transferService.transfer(7L, new TransferRequest(1L, 2L, new BigDecimal("10.00"), null))
        );

        assertEquals("CURRENCY_MISMATCH", ex.getCode());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void transferShouldThrowWhenFundsAreInsufficient() {
        Account from = Account.builder().id(1L).userId(7L).currency(AccountCurrency.KZT).balance(new BigDecimal("5.00")).build();
        Account to = Account.builder().id(2L).userId(9L).currency(AccountCurrency.KZT).balance(new BigDecimal("250.00")).build();

        when(accountService.getOwned(7L, 1L)).thenReturn(from);
        when(accountService.getById(2L)).thenReturn(to);

        BusinessException ex = assertThrows(
                BusinessException.class,
                () -> transferService.transfer(7L, new TransferRequest(1L, 2L, new BigDecimal("10.00"), null))
        );

        assertEquals("INSUFFICIENT_FUNDS", ex.getCode());
        verify(transactionRepository, never()).save(any());
    }
}
