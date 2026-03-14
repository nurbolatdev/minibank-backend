package com.example.minibankbackend.transaction.controller;



import com.example.minibankbackend.common.response.PageResponse;
import com.example.minibankbackend.transaction.dto.TransactionResponse;
import com.example.minibankbackend.transaction.service.TransactionQueryService;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionQueryService service;

    public TransactionController(TransactionQueryService service) {
        this.service = service;
    }

    @GetMapping
    public PageResponse<TransactionResponse> list(
            @RequestParam Long accountId,
            @RequestParam(required=false) Instant from,
            @RequestParam(required=false) Instant to,
            @RequestParam(defaultValue="0") int page,
            @RequestParam(defaultValue="10") int size
    ) {
        return service.list(accountId, from, to, page, size);
    }
}
