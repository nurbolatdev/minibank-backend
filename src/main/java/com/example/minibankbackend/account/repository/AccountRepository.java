package com.example.minibankbackend.account.repository;



import com.example.minibankbackend.account.model.Account;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Page<Account> findByUserId(Long userId, Pageable pageable);
    Optional<Account> findByIdAndUserId(Long id, Long userId);
}
