package com.example.minibankbackend.transaction.repository;



import com.example.minibankbackend.transaction.model.Transaction;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Page<Transaction> findByAccountIdAndCreatedAtBetween(Long accountId, Instant from, Instant to, Pageable pageable);
    Page<Transaction> findByAccountId(Long accountId, Pageable pageable);
}
