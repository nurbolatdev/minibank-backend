package com.example.minibankbackend.transaction.service;



import com.example.minibankbackend.common.response.PageResponse;
import com.example.minibankbackend.transaction.dto.TransactionResponse;
import com.example.minibankbackend.transaction.repository.TransactionRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class TransactionQueryService {

    private final TransactionRepository repo;

    public TransactionQueryService(TransactionRepository repo) { this.repo = repo; }

    public PageResponse<TransactionResponse> list(Long accountId, Instant from, Instant to, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        Page<com.example.minibankbackend.transaction.model.Transaction> p;
        if (from != null && to != null) {
            p = repo.findByAccountIdAndCreatedAtBetween(accountId, from, to, pageable);
        } else {
            p = repo.findByAccountId(accountId, pageable);
        }

        return new PageResponse<>(
                p.getContent().stream().map(t -> new TransactionResponse(
                        t.getId(), t.getAccountId(), t.getType(), t.getStatus(), t.getAmount(), t.getDescription(), t.getCreatedAt()
                )).toList(),
                p.getNumber(), p.getSize(), p.getTotalElements(), p.getTotalPages()
        );
    }
}
