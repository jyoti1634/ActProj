package com.example.accounting.service;

import com.example.accounting.entity.Ledger;

import java.util.List;
import java.util.Optional;

public interface LedgerService {
    Ledger createEntry(Ledger ledger);
    Optional<Ledger> findById(Integer id);
    List<Ledger> findByMonthId(Integer monthId);
}
