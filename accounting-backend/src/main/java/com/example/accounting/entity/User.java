// Path: accounting-backend/src/main/java/com/example/accounting/entity/User.java
package com.example.accounting.entity;

// JPA and Lombok imports for entity definition and boilerplate code reduction
// This single import gives access to annotations like
// JPA (Java Persistence API) is a standard way to store and retrieve Java objects from a relational database.
import jakarta.persistence.*;
import lombok.*;

// Entity class representing a User in the accounting system
@Entity
// Table annotation to specify table name and unique constraints
@Table(name = "users", uniqueConstraints = {@UniqueConstraint(columnNames = {"email"})})
// Lombok annotations to generate getters, setters, constructors, and builder pattern
@Data
// Lombok annotations for constructors without args
@NoArgsConstructor
// Lombok annotation for all-args constructor
@AllArgsConstructor
// Lombok annotation for builder pattern
@Builder
// Public class definition
public class User {
    // Primary key field with auto-generation strategy
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    // Column definitions with constraints
    @Column(name = "username", length = 100, nullable = false)
    private String username;
    // Email field with unique constraint
    @Column(name = "email", length = 150, unique = true)
    private String email;
    // Password field
    @Column(name = "password", length = 255, nullable = false)
    private String password;
}