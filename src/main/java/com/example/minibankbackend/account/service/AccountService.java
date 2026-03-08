package com.example.minibankbackend.account.service;



import com.example.minibankbackend.account.model.*;
import com.example.minibankbackend.account.dto.*;
import com.example.minibankbackend.account.repository.AccountRepository;
import com.example.minibankbackend.common.exception.NotFoundException;
import com.example.minibankbackend.common.response.PageResponse;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class AccountService {

    private final AccountRepository repo;

    public AccountService(AccountRepository repo) {
        this.repo = repo;
    }

    public AccountResponse create(Long userId, CreateAccountRequest req) {
        Account acc = Account.builder()
                .userId(userId)
                .currency(req.currency())
                .balance(BigDecimal.ZERO)
                .build();
        acc = repo.save(acc);
        return new AccountResponse(acc.getId(), acc.getCurrency(), acc.getBalance());
    }

    public PageResponse<AccountResponse> myAccounts(Long userId, int page, int size) {
        Page<Account> p = repo.findByUserId(userId, PageRequest.of(page, size, Sort.by("id").descending()));
        return new PageResponse<>(
                p.getContent().stream().map(a -> new AccountResponse(a.getId(), a.getCurrency(), a.getBalance())).toList(),
                p.getNumber(), p.getSize(), p.getTotalElements(), p.getTotalPages()
        );
    }

    public Account getOwned(Long userId, Long accountId) {
        return repo.findByIdAndUserId(accountId, userId)
                .orElseThrow(() -> new NotFoundException("Account not found"));
    }

    public Account getById(Long id) {
        return repo.findById(id).orElseThrow(() -> new NotFoundException("Account not found"));
    }

    public void save(Account a) { repo.save(a); }
}
