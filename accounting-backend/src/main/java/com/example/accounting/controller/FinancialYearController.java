package com.example.accounting.controller;

import com.example.accounting.dto.FinancialYearDto;
import com.example.accounting.entity.Account;
import com.example.accounting.entity.FinancialYear;
import com.example.accounting.mapper.FinancialYearMapper;
import com.example.accounting.service.AccountService;
import com.example.accounting.service.FinancialYearService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
        Account account = accountService.findById(accountId).orElseThrow(() -> new IllegalArgumentException("Account not found"));
        FinancialYear fy = FinancialYearMapper.toEntity(dto);
        fy.setAccount(account);
        FinancialYear saved = financialYearService.createFinancialYear(fy);
        return ResponseEntity.ok(FinancialYearMapper.toDto(saved));
    }
    // Endpoint for listing financial years associated with a specific account
    @GetMapping
    public ResponseEntity<List<FinancialYearDto>> list(@PathVariable Integer accountId) {
        List<FinancialYearDto> dtos = financialYearService.findByAccountId(accountId).stream().map(FinancialYearMapper::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
}
