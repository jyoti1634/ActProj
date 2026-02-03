package com.example.accounting.repository;

import com.example.accounting.entity.Ledger;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LedgerRepository extends JpaRepository<Ledger, Integer> {
    List<Ledger> findByMonthId(Integer monthId);
}