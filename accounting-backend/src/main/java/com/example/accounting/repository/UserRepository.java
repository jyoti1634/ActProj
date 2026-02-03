// declares the package for the UserRepository
package com.example.accounting.repository;
// imports user entity to manage user data
import com.example.accounting.entity.User;
// imports JpaRepository for CRUD operations
import org.springframework.data.jpa.repository.JpaRepository;
// imports Optional for handling potential null values
import java.util.Optional;

// Repository interface for User entity extending JpaRepository
public interface UserRepository extends JpaRepository<User, Integer> {
    // Method to find a user by username
    Optional<User> findByUsername(String username);
    // Method to find a user by email
    Optional<User> findByEmail(String email);
}