package com.example.accounting.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "accounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {
    // Primary key field with auto-generation strategy
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    // Many-to-one relationship with User entity and foreign key mapping 
    // to user_id column in accounts table
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    // Column definition for account name with constraints
    @Column(name = "account_name", length = 200, nullable = false)
    private String accountName;
}