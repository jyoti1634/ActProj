package com.example.accounting.controller;

import com.example.accounting.dto.LedgerDto;
import com.example.accounting.entity.Ledger;
import com.example.accounting.entity.Month;
import com.example.accounting.mapper.LedgerMapper;
import com.example.accounting.service.LedgerService;
import com.example.accounting.service.MonthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/months/{monthId}/ledger")
@RequiredArgsConstructor
@Validated
public class LedgerController {

    private final LedgerService ledgerService;
    private final MonthService monthService;
    // Endpoint for creating a new ledger entry for a specific month
    @PostMapping
    public ResponseEntity<LedgerDto> create(@PathVariable Integer monthId, @Valid @RequestBody LedgerDto dto) {
        Month month = monthService.findById(monthId).orElseThrow(() -> new IllegalArgumentException("Month not found"));
        Ledger l = LedgerMapper.toEntity(dto);
        l.setMonth(month);
        Ledger saved = ledgerService.createEntry(l);
        return ResponseEntity.ok(LedgerMapper.toDto(saved));
    }
    // Endpoint for listing ledger entries associated with a specific month
    @GetMapping
    public ResponseEntity<List<LedgerDto>> list(@PathVariable Integer monthId) {
        List<LedgerDto> dtos = ledgerService.findByMonthId(monthId).stream().map(LedgerMapper::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // Endpoint for deleting a specific ledger entry within a month
    @DeleteMapping("/{entryId}")
    public ResponseEntity<Void> delete(@PathVariable Integer monthId, @PathVariable Integer entryId) {
        Ledger ledger = ledgerService.findById(entryId).orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Ledger entry not found"));
        if (ledger.getMonth() == null || !ledger.getMonth().getId().equals(monthId)) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "Ledger entry does not belong to specified month");
        }
        ledgerService.deleteById(entryId);
        return ResponseEntity.noContent().build();
    }
}
