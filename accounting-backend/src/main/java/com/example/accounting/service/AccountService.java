package com.example.accounting.service;

import com.example.accounting.entity.Account;

import java.util.List;
import java.util.Optional;

public interface AccountService {
    Account createAccount(Account account);
    Optional<Account> findById(Integer id);
    List<Account> findByUserId(Integer userId);

    // Check duplicate by user and account name (case-insensitive)
    boolean existsByUserIdAndAccountNameIgnoreCase(Integer userId, String accountName);

    // Ownership-aware lookup
    Optional<Account> findByIdAndUserId(Integer id, Integer userId);
}
