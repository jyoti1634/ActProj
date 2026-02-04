package com.example.accounting.service.impl;

import com.example.accounting.entity.Ledger;
import com.example.accounting.entity.Month;
import com.example.accounting.repository.LedgerRepository;
import com.example.accounting.repository.MonthRepository;
import com.example.accounting.service.MonthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class MonthServiceImpl implements MonthService {
    // Repository dependencies for Month and Ledger entities
    private final MonthRepository monthRepository;
    private final LedgerRepository ledgerRepository;
    // Creates a new month after validating the input data
    @Override
    public Month createMonth(Month month) {
        if (month.getMonthName() == null || month.getMonthName().isBlank()) {
            throw new IllegalArgumentException("monthName is required");
        }
        return monthRepository.save(month);
    }
    // Finds a month by its ID
    @Override
    @Transactional(readOnly = true)
    public Optional<Month> findById(Integer id) {
        return monthRepository.findById(id);
    }

    // Ownership-aware lookup: month must belong to a year whose account belongs to the user
    @Override
    @Transactional(readOnly = true)
    public Optional<Month> findByIdAndYearAccountUserId(Integer id, Integer userId) {
        return monthRepository.findByIdAndYear_Account_User_Id(id, userId);
    }

    // Finds all months associated with a specific financial year ID
    @Override
    @Transactional(readOnly = true)
    public List<Month> findByYearId(Integer yearId) {
        return monthRepository.findByYearId(yearId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Month> findByYearIdAndUserId(Integer yearId, Integer userId) {
        return monthRepository.findByYearIdAndYear_Account_User_Id(yearId, userId);
    }

    // Update an existing Month (used to set opening balance etc.)
    @Override
    public Month updateMonth(Month month) {
        return monthRepository.save(month);
    }

    // Calculates the closing balance for a given month based on ledger entries
    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateClosingBalance(Integer monthId) {
        // Calculate the closing balance by summing and subtracting relevant ledger amounts
        List<Ledger> entries = ledgerRepository.findByMonthId(monthId);
        BigDecimal total = BigDecimal.ZERO;
        // Iterate through ledger entries to compute the closing balance
        for (Ledger l : entries) {
            // Add cash and cash bank amounts, subtract expense amounts
            if (l.getCashAmt() != null) total = total.add(l.getCashAmt());
            if (l.getCshbankAmt() != null) total = total.add(l.getCshbankAmt());
            if (l.getExpAmt() != null) total = total.subtract(l.getExpAmt());
            if (l.getExpbankAmt() != null) total = total.subtract(l.getExpbankAmt());
        }
        return total;
    }
}
