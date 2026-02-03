package com.example.accounting.controller;

import com.example.accounting.dto.MonthDto;
import com.example.accounting.entity.FinancialYear;
import com.example.accounting.entity.Month;
import com.example.accounting.mapper.MonthMapper;
import com.example.accounting.service.FinancialYearService;
import com.example.accounting.service.MonthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
        FinancialYear fy = financialYearService.findById(yearId).orElseThrow(() -> new IllegalArgumentException("FinancialYear not found"));
        Month m = MonthMapper.toEntity(dto);
        m.setYear(fy);
        Month saved = monthService.createMonth(m);
        return ResponseEntity.ok(MonthMapper.toDto(saved));
    }
    // Endpoint for listing months associated with a specific financial year
    @GetMapping
    public ResponseEntity<List<MonthDto>> list(@PathVariable Integer yearId) {
        List<MonthDto> dtos = monthService.findByYearId(yearId).stream().map(MonthMapper::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
}
