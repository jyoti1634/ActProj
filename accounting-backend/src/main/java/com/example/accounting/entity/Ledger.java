package com.example.accounting.entity;

import jakarta.persistence.*;
import lombok.*;
// Hibernate import for automatic timestamping
import org.hibernate.annotations.CreationTimestamp;
// Java imports for data types
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "ledger")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ledger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "month_id", nullable = false)
    private Month month;

    @Column(name = "entry_date", nullable = false)
    private LocalDate entryDate;

    @Column(name = "particular_csh", length = 255)
    private String particularCsh;

    @Column(name = "particular_exp", length = 255)
    private String particularExp;

    @Column(name = "cash_amt", precision = 12, scale = 2)
    private BigDecimal cashAmt = new BigDecimal("0.00");

    @Column(name = "exp_amt", precision = 12, scale = 2, nullable = false)
    private BigDecimal expAmt = new BigDecimal("0.00");

    @Column(name = "cshbank_amt", precision = 12, scale = 2)
    private BigDecimal cshbankAmt;

    @Column(name = "expbank_amt", precision = 12, scale = 2)
    private BigDecimal expbankAmt;

    @Column(name = "classification_csh", length = 100)
    private String classificationCsh;

    @Column(name = "classification_exp", length = 255)
    private String classificationExp;

    @Column(name = "cheque_no", length = 50)
    private String chequeNo;
    // Automatic timestamping of record creation
    // creationtimestamp will auto-fill this field with the current timestamp when the record is created
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}