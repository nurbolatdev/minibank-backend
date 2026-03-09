package com.example.minibankbackend.account.controller;


import com.example.minibankbackend.account.dto.*;
import com.example.minibankbackend.account.service.AccountService;
import com.example.minibankbackend.common.response.PageResponse;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService service;

    public AccountController(AccountService service) {
        this.service = service;
    }

    @PostMapping
    public AccountResponse create(Authentication auth, @Valid @RequestBody CreateAccountRequest req) {
        Long userId = Long.valueOf(auth.getName());
        return service.create(userId, req);
    }

    @GetMapping
    public PageResponse<AccountResponse> my(Authentication auth,
                                            @RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "10") int size) {
        Long userId = Long.valueOf(auth.getName());
        return service.myAccounts(userId, page, size);
    }
}