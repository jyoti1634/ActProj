package com.example.accounting.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class MonthDto {
    private Integer id;

    @NotNull
    private Integer yearId;

    @NotBlank
    private String monthName;

    private BigDecimal openingBalance;
}
