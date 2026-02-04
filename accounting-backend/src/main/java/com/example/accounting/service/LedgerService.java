package com.example.accounting.service;

import com.example.accounting.entity.Ledger;

import java.util.List;
import java.util.Optional;

public interface LedgerService {
    Ledger createEntry(Ledger ledger);
    Optional<Ledger> findById(Integer id);
    List<Ledger> findByMonthId(Integer monthId);
    // Deletes a ledger entry by its ID and returns nothing. Implementations should
    // ensure related month balances are recalculated and persisted as needed.
    void deleteById(Integer id);
}
