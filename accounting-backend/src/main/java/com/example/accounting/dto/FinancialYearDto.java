package com.example.accounting.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class FinancialYearDto {
    private Integer id;

    @NotNull
    private Integer accountId;

    @NotNull
    private Integer yearStart;

    @NotNull
    private Integer yearEnd;

    private BigDecimal openingBalance;
}
