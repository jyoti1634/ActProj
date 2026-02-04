package com.example.accounting.service.impl;

import com.example.accounting.entity.Ledger;
import com.example.accounting.repository.LedgerRepository;
import com.example.accounting.service.LedgerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class LedgerServiceImpl implements LedgerService {
    // Repository dependency for Ledger entity
    private final LedgerRepository ledgerRepository;
    // Service dependency for Month operations (used to recalculate balances)
    private final com.example.accounting.service.MonthService monthService;
    // Creates a new ledger entry after validating the input data
    @Override
    public Ledger createEntry(Ledger ledger) {
        // Add simple validation
        if (ledger.getEntryDate() == null) {
            throw new IllegalArgumentException("entryDate is required");
        }
        return ledgerRepository.save(ledger);
    }
    // Finds a ledger entry by its ID
    @Override
    @Transactional(readOnly = true)
    public Optional<Ledger> findById(Integer id) {
        return ledgerRepository.findById(id);
    }
    // Finds all ledger entries associated with a specific month ID
    @Override
    @Transactional(readOnly = true)
    public List<Ledger> findByMonthId(Integer monthId) {
        return ledgerRepository.findByMonthId(monthId);
    }

    // Deletes a ledger entry by ID, recalculates related month balances and persists them
    @Override
    public void deleteById(Integer id) {
        Ledger l = ledgerRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Ledger not found"));
        // capture month id before deletion
        Integer monthId = l.getMonth() != null ? l.getMonth().getId() : null;
        ledgerRepository.delete(l);
        // Recalculate closing balance for the month and persist it if month exists
        if (monthId != null) {
            java.math.BigDecimal closing = monthService.calculateClosingBalance(monthId);
            com.example.accounting.entity.Month m = monthService.findById(monthId).orElseThrow(() -> new IllegalArgumentException("Month not found"));
            m.setClosingBalance(closing);
            monthService.updateMonth(m);
        }
    }
}
