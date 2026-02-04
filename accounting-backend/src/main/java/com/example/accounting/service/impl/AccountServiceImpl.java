package com.example.accounting.service.impl;

import com.example.accounting.entity.Account;
import com.example.accounting.repository.AccountRepository;
import com.example.accounting.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    // Creates a new account after validating the input data 
    @Override
    public Account createAccount(Account account) {
        // Minimal validation
        if (account.getAccountName() == null || account.getAccountName().isBlank()) {
            throw new IllegalArgumentException("Account name is required");
        }
        // Prevent duplicates per user (case-insensitive)
        if (account.getUser() != null && account.getUser().getId() != null) {
            boolean exists = accountRepository.existsByUserIdAndAccountNameIgnoreCase(account.getUser().getId(), account.getAccountName());
            if (exists) {
                throw new IllegalStateException("Account with this name already exists for the user");
            }
        }
        return accountRepository.save(account);
    }

    @Override
    public boolean existsByUserIdAndAccountNameIgnoreCase(Integer userId, String accountName) {
        return accountRepository.existsByUserIdAndAccountNameIgnoreCase(userId, accountName);
    }
    // Finds an account by its ID 
    @Override
    @Transactional(readOnly = true)
    public Optional<Account> findById(Integer id) {
        return accountRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Account> findByIdAndUserId(Integer id, Integer userId) {
        return accountRepository.findByIdAndUserId(id, userId);
    }

    // Finds all accounts associated with a specific user ID
    @Override
    @Transactional(readOnly = true)
    public List<Account> findByUserId(Integer userId) {
        return accountRepository.findByUserId(userId);
    }
}
