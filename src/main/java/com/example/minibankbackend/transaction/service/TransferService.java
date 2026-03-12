package com.example.minibankbackend.transaction.service;


import com.example.minibankbackend.account.model.Account;
    import com.example.minibankbackend.account.service.AccountService;
import com.example.minibankbackend.common.exception.BusinessException;
import com.example.minibankbackend.transaction.model.*;
import com.example.minibankbackend.transaction.dto.TransferRequest;
import com.example.minibankbackend.transaction.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;

@Service
public class TransferService {

    private final AccountService accountService;
    private final TransactionRepository txRepo;

    public TransferService(AccountService accountService, TransactionRepository txRepo) {
        this.accountService = accountService;
        this.txRepo = txRepo;
    }

    @Transactional
    public void transfer(Long userId, TransferRequest req) {
        if (req.fromAccountId().equals(req.toAccountId())) {
            throw new BusinessException("SAME_ACCOUNT", "fromAccountId and toAccountId must be different");
        }

        Account from = accountService.getOwned(userId, req.fromAccountId());
        Account to = accountService.getById(req.toAccountId());

        if (from.getCurrency() != to.getCurrency()) {
            throw new BusinessException("CURRENCY_MISMATCH", "Accounts have different currencies");
        }

        BigDecimal amount = req.amount();
        if (from.getBalance().compareTo(amount) < 0) {
            throw new BusinessException("INSUFFICIENT_FUNDS", "Not enough balance");
        }

        from.setBalance(from.getBalance().subtract(amount));
        to.setBalance(to.getBalance().add(amount));

        accountService.save(from);
        accountService.save(to);

        Instant now = Instant.now();

        txRepo.save(Transaction.builder()
                .accountId(from.getId())
                .type(TxType.TRANSFER_OUT)
                .status(TxStatus.SUCCESS)
                .amount(amount)
                .description(req.description())
                .createdAt(now)
                .build());

        txRepo.save(Transaction.builder()
                .accountId(to.getId())
                .type(TxType.TRANSFER_IN)
                .status(TxStatus.SUCCESS)
                .amount(amount)
                .description(req.description())
                .createdAt(now)
                .build());
    }
}