package com.example.accounting.controller;

import com.example.accounting.dto.AccountDto;
import com.example.accounting.entity.Account;
import com.example.accounting.entity.User;
import com.example.accounting.mapper.AccountMapper;
import com.example.accounting.service.AccountService;
import com.example.accounting.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
// The @RequestMapping annotation is part of Spring Web. It is used to map web requests to specific handler classes or methods.
import org.springframework.web.bind.annotation.*;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.example.accounting.security.UserPrincipal;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
@Validated
public class AccountController {
    // Service for account management
    private final AccountService accountService;
    // Service for user management
    private final UserService userService;
    // Endpoint for creating a new account (uses authenticated user)
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody AccountDto dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof UserPrincipal)) {
            return ResponseEntity.status(401).build();
        }
        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        User user = userService.findById(principal.getId()).orElseThrow(() -> new IllegalArgumentException("User not found"));
        // prevent duplicate account names for the same user
        if (dto.getAccountName() != null && accountService.existsByUserIdAndAccountNameIgnoreCase(user.getId(), dto.getAccountName())) {
            return ResponseEntity.status(409).body(java.util.Map.of("message", "Account with this name already exists"));
        }
        Account account = AccountMapper.toEntity(dto);
        account.setUser(user);
        Account saved = accountService.createAccount(account);
        return ResponseEntity.ok(AccountMapper.toDto(saved));
    }
    // Endpoint for listing accounts, optionally filtered by userId
    // @GetMapping
    // public ResponseEntity<List<AccountDto>> list(@RequestParam(required = false) Integer userId) {
    //     List<AccountDto> dtos;
    //     if (userId != null) {
    //         dtos = accountService.findByUserId(userId).stream().map(AccountMapper::toDto).collect(Collectors.toList());
    //     } else {
    //         dtos = accountService.findByUserId(null).stream().map(AccountMapper::toDto).collect(Collectors.toList());
    //     }
    //     return ResponseEntity.ok(dtos);
    // }
    @GetMapping
public ResponseEntity<List<AccountDto>> list(@RequestParam(required = false) Integer userId) {
    List<AccountDto> dtos;
    Integer effectiveUserId = userId;
    if (effectiveUserId == null) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserPrincipal) {
            effectiveUserId = ((UserPrincipal) auth.getPrincipal()).getId();
        }
    }

    if (effectiveUserId != null) {
        dtos = accountService.findByUserId(effectiveUserId).stream()
                .peek(account -> System.out.println("Original Account: " + account)) // Step 1: stream elements
                .map(account -> {
                    AccountDto dto = AccountMapper.toDto(account);
                    System.out.println("Mapped to DTO: " + dto); // Step 2: map
                    return dto;
                })
                .collect(Collectors.toList()); // Step 3: collect into a list
    } else {
        // If no user could be determined, return empty list
        dtos = List.of();
    }

    System.out.println("Final DTO List: " + dtos); // Step 4: final collected list
    return ResponseEntity.ok(dtos);
}

}
