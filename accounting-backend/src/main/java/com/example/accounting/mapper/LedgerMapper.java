package com.example.accounting.mapper;

import com.example.accounting.dto.LedgerDto;
import com.example.accounting.entity.Ledger;

public final class LedgerMapper {
    private LedgerMapper() {}

    public static LedgerDto toDto(Ledger l) {
        if (l == null) return null;
        LedgerDto dto = new LedgerDto();
        dto.setId(l.getId());
        dto.setMonthId(l.getMonth() != null ? l.getMonth().getId() : null);
        dto.setEntryDate(l.getEntryDate());
        dto.setParticularCsh(l.getParticularCsh());
        dto.setParticularExp(l.getParticularExp());
        dto.setCashAmt(l.getCashAmt());
        dto.setExpAmt(l.getExpAmt());
        dto.setCshbankAmt(l.getCshbankAmt());
        dto.setExpbankAmt(l.getExpbankAmt());
        dto.setClassificationCsh(l.getClassificationCsh());
        dto.setClassificationExp(l.getClassificationExp());
        dto.setChequeNo(l.getChequeNo());
        dto.setCreatedAt(l.getCreatedAt());
        return dto;
    }

    public static Ledger toEntity(LedgerDto d) {
        if (d == null) return null;
        Ledger l = Ledger.builder()
                .entryDate(d.getEntryDate())
                .particularCsh(d.getParticularCsh())
                .particularExp(d.getParticularExp())
                .cashAmt(d.getCashAmt())
                .expAmt(d.getExpAmt())
                .cshbankAmt(d.getCshbankAmt())
                .expbankAmt(d.getExpbankAmt())
                .classificationCsh(d.getClassificationCsh())
                .classificationExp(d.getClassificationExp())
                .chequeNo(d.getChequeNo())
                .build();
        return l;
    }
}
