package com.example.accounting.service;

import com.example.accounting.entity.Month;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface MonthService {
    Month createMonth(Month month);
    Optional<Month> findById(Integer id);
    List<Month> findByYearId(Integer yearId);
    BigDecimal calculateClosingBalance(Integer monthId);
    Month updateMonth(Month month); // new
}
