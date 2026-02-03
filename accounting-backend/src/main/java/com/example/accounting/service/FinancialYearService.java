package com.example.accounting.service;

import com.example.accounting.entity.FinancialYear;

import java.util.List;
import java.util.Optional;

public interface FinancialYearService {
    FinancialYear createFinancialYear(FinancialYear year);
    Optional<FinancialYear> findById(Integer id);
    List<FinancialYear> findByAccountId(Integer accountId);
}
