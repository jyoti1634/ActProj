package com.example.accounting.mapper;

import com.example.accounting.dto.AccountDto;
import com.example.accounting.entity.Account;

// Utility class for mapping Account entity to AccountDto and vice versa
public final class AccountMapper {
    // Private constructor to prevent instantiation
    private AccountMapper() {}
    // Converts an Account entity to an AccountDto
    public static AccountDto toDto(Account account) {
        if (account == null) return null;
        AccountDto dto = new AccountDto();
        dto.setId(account.getId());
        dto.setAccountName(account.getAccountName());
        dto.setUserId(account.getUser() != null ? account.getUser().getId() : null);
        return dto;
    }

    /**
     * Note: this does not set relationships (user) — service must set them.
     */
    // Converts an AccountDto to an Account entity
    public static Account toEntity(AccountDto dto) {
        // Return null if dto is null, else create and return Account entity
        if (dto == null) return null;
        // Map fields from AccountDto to Account entity
        Account a = Account.builder()
                .accountName(dto.getAccountName())
                .build();
        return a;
    }
}
