package com.example.accounting.mapper;

import com.example.accounting.dto.MonthDto;
import com.example.accounting.entity.Month;

public final class MonthMapper {
    private MonthMapper() {}

    public static MonthDto toDto(Month month) {
        if (month == null) return null;
        MonthDto dto = new MonthDto();
        dto.setId(month.getId());
        dto.setYearId(month.getYear() != null ? month.getYear().getId() : null);
        dto.setMonthName(month.getMonthName());
        dto.setOpeningBalance(month.getOpeningBalance());
        return dto;
    }

    public static Month toEntity(MonthDto dto) {
        if (dto == null) return null;
        Month m = Month.builder()
                .monthName(dto.getMonthName())
                .openingBalance(dto.getOpeningBalance())
                .build();
        return m;
    }
}
