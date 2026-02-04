package com.example.accounting.controller;

import com.example.accounting.dto.FinancialYearDto;
import com.example.accounting.entity.Account;
import com.example.accounting.entity.FinancialYear;
import com.example.accounting.exception.ResourceNotFoundException;
import com.example.accounting.mapper.FinancialYearMapper;
import com.example.accounting.security.UserPrincipal;
import com.example.accounting.service.AccountService;
import com.example.accounting.service.FinancialYearService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/accounts/{accountId}/years")
@RequiredArgsConstructor
@Validated
public class FinancialYearController {

    private final FinancialYearService financialYearService;
    private final AccountService accountService;
    // Endpoint for creating a new financial year for a specific account
    @PostMapping
    public ResponseEntity<FinancialYearDto> create(@PathVariable Integer accountId, @Valid @RequestBody FinancialYearDto dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof UserPrincipal)) {
            return ResponseEntity.status(401).build();
        }
        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        // ensure the account belongs to the authenticated user
        var account = accountService.findByIdAndUserId(accountId, principal.getId()).orElseThrow(() -> new ResourceNotFoundException("Account not found"));
        FinancialYear fy = FinancialYearMapper.toEntity(dto);
        fy.setAccount(account);
        FinancialYear saved = financialYearService.createFinancialYear(fy);
        return ResponseEntity.ok(FinancialYearMapper.toDto(saved));
    }
    // Endpoint for listing financial years associated with a specific account
    @GetMapping
    public ResponseEntity<List<FinancialYearDto>> list(@PathVariable Integer accountId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof UserPrincipal)) {
            return ResponseEntity.status(401).build();
        }
        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        List<FinancialYearDto> dtos = financialYearService.findByAccountIdAndUserId(accountId, principal.getId()).stream().map(FinancialYearMapper::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
}
