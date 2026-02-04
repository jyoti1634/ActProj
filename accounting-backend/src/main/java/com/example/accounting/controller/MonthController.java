package com.example.accounting.controller;

import com.example.accounting.dto.MonthDto;
import com.example.accounting.entity.FinancialYear;
import com.example.accounting.entity.Month;
import com.example.accounting.dto.MonthDto;
import com.example.accounting.entity.FinancialYear;
import com.example.accounting.entity.Month;
import com.example.accounting.exception.ResourceNotFoundException;
import com.example.accounting.mapper.MonthMapper;
import com.example.accounting.security.UserPrincipal;
import com.example.accounting.service.FinancialYearService;
import com.example.accounting.service.MonthService;
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
@RequestMapping("/api/v1/years/{yearId}/months")
@RequiredArgsConstructor
@Validated
public class MonthController {

    private final MonthService monthService;
    private final FinancialYearService financialYearService;
    // Endpoint for creating a new month for a specific financial year
    @PostMapping
    public ResponseEntity<MonthDto> create(@PathVariable Integer yearId, @Valid @RequestBody MonthDto dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof UserPrincipal)) {
            return ResponseEntity.status(401).build();
        }
        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        FinancialYear fy = financialYearService.findByIdAndAccountUserId(yearId, principal.getId()).orElseThrow(() -> new ResourceNotFoundException("FinancialYear not found"));
        Month m = MonthMapper.toEntity(dto);
        m.setYear(fy);
        Month saved = monthService.createMonth(m);
        return ResponseEntity.ok(MonthMapper.toDto(saved));
    }
    // Endpoint for listing months associated with a specific financial year
    @GetMapping
    public ResponseEntity<List<MonthDto>> list(@PathVariable Integer yearId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof UserPrincipal)) {
            return ResponseEntity.status(401).build();
        }
        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        List<MonthDto> dtos = monthService.findByYearIdAndUserId(yearId, principal.getId()).stream().map(MonthMapper::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // Endpoint for updating a month (e.g., openingBalance, closingBalance)
    @PatchMapping("/{monthId}")
    public ResponseEntity<MonthDto> update(@PathVariable Integer yearId, @PathVariable Integer monthId, @RequestBody MonthDto dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof UserPrincipal)) {
            return ResponseEntity.status(401).build();
        }
        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        Month m = monthService.findByIdAndYearAccountUserId(monthId, principal.getId()).orElseThrow(() -> new ResourceNotFoundException("Month not found"));
        if (dto.getOpeningBalance() != null) m.setOpeningBalance(dto.getOpeningBalance());
        if (dto.getClosingBalance() != null) m.setClosingBalance(dto.getClosingBalance());
        Month saved = monthService.updateMonth(m);
        return ResponseEntity.ok(MonthMapper.toDto(saved));
    }
}
