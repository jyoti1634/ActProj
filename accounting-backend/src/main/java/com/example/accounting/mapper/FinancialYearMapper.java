package com.example.accounting.mapper;

import com.example.accounting.dto.FinancialYearDto;
import com.example.accounting.entity.FinancialYear;

public final class FinancialYearMapper {
    private FinancialYearMapper() {}

    public static FinancialYearDto toDto(FinancialYear year) {
        if (year == null) return null;
        FinancialYearDto dto = new FinancialYearDto();
        dto.setId(year.getId());
        dto.setAccountId(year.getAccount() != null ? year.getAccount().getId() : null);
        dto.setYearStart(year.getYearStart());
        dto.setYearEnd(year.getYearEnd());
        dto.setOpeningBalance(year.getOpeningBalance());
        return dto;
    }

    public static FinancialYear toEntity(FinancialYearDto dto) {
        if (dto == null) return null;
        FinancialYear fy = FinancialYear.builder()
                .yearStart(dto.getYearStart())
                .yearEnd(dto.getYearEnd())
                .openingBalance(dto.getOpeningBalance())
                .build();
        return fy;
    }
}
