package com.example.accounting.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "financial_years", uniqueConstraints = {@UniqueConstraint(columnNames = {"account_id", "year_end", "year_start"})})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FinancialYear {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Column(name = "year_start", nullable = false)
    private Integer yearStart;

    @Column(name = "year_end", nullable = false)
    private Integer yearEnd;
    // Opening balance field with precision and scale, defaulting to 0.00
    @Column(name = "opening_balance", precision = 12, scale = 2)
    private BigDecimal openingBalance = new BigDecimal("0.00");
}