package com.example.accounting.repository;

import com.example.accounting.entity.FinancialYear;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FinancialYearRepository extends JpaRepository<FinancialYear, Integer> {
    List<FinancialYear> findByAccountId(Integer accountId);
}