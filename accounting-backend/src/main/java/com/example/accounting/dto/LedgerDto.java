package com.example.accounting.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class LedgerDto {
    private Integer id;

    @NotNull
    private Integer monthId;

    @NotNull
    private LocalDate entryDate;

    private String particularCsh;
    private String particularExp;

    private BigDecimal cashAmt;

    @NotNull
    private BigDecimal expAmt;

    private BigDecimal cshbankAmt;
    private BigDecimal expbankAmt;

    private String classificationCsh;
    private String classificationExp;
    private String chequeNo;

    private LocalDateTime createdAt;
}
