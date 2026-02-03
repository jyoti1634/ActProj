package com.example.accounting.service.impl;

import com.example.accounting.entity.FinancialYear;
import com.example.accounting.entity.Month;
import com.example.accounting.repository.FinancialYearRepository;
import com.example.accounting.repository.MonthRepository;
import com.example.accounting.service.FinancialYearService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class FinancialYearServiceImpl implements FinancialYearService {

    private final FinancialYearRepository financialYearRepository;
    private final MonthRepository monthRepository;
    // Creates a new financial year and auto-generates associated months
    @Override
    public FinancialYear createFinancialYear(FinancialYear year) {
        // Basic validation
        if (year.getYearStart() > year.getYearEnd()) {
            throw new IllegalArgumentException("yearStart must be <= yearEnd");
        }
        FinancialYear saved = financialYearRepository.save(year);
        // Auto-create months (simple 12-month placeholder from April to March)
        List<Month> months = new ArrayList<>();
        // List of month names from April to March
        String[] monthNames = {"April","May","June","July","August","September","October","November","December","January","February","March"};
        // The FinancialYear service coordinates month creation because months are a mandatory part of a financial year, and keeping this logic inside the service ensures transactional consistency and enforces business rules.
        // Create Month entities for each month name and associate with the saved financial year
        for (String m : monthNames) {
            Month month = Month.builder()
                    .year(saved)
                    .monthName(m)
                    .openingBalance(new java.math.BigDecimal("0.00"))
                    .build();
            months.add(month);
        }
        monthRepository.saveAll(months);
        return saved;
    }
    // Finds a financial year by its ID
    @Override
    @Transactional(readOnly = true)
    public Optional<FinancialYear> findById(Integer id) {
        return financialYearRepository.findById(id);
    }
    // Finds all financial years associated with a specific account ID
    @Override
    @Transactional(readOnly = true)
    public List<FinancialYear> findByAccountId(Integer accountId) {
        return financialYearRepository.findByAccountId(accountId);
    }
}
