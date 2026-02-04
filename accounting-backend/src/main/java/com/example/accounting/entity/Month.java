package com.example.accounting.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "months", uniqueConstraints = {@UniqueConstraint(columnNames = {"year_id", "month_name"})})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Month {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "year_id", nullable = false)
    private FinancialYear year;

    @Column(name = "month_name", length = 20, nullable = false)
    private String monthName;

    // Opening balance field with precision and scale, defaulting to 0.00
    @Builder.Default
    @Column(name = "opening_balance", precision = 12, scale = 2)
    private BigDecimal openingBalance = new BigDecimal("0.00");

    // Closing balance field (stored) to make recalculation results persistent
    @Builder.Default
    @Column(name = "closing_balance", precision = 12, scale = 2)
    private BigDecimal closingBalance = new BigDecimal("0.00");
}