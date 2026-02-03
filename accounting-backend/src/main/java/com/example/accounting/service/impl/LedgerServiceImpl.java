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
}
