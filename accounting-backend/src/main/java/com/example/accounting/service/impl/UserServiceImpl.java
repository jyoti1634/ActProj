// Declares the package for the UserServiceImpl class
package com.example.accounting.service.impl;

// imports User entity to manage user data
import com.example.accounting.entity.User;
// imports UserRepository to interact with the database
import com.example.accounting.repository.UserRepository;
// imports UserService interface to implement its methods
import com.example.accounting.service.UserService;
// imports Lombok annotations for reducing boilerplate code
import lombok.RequiredArgsConstructor;
// imports Spring Security's BCryptPasswordEncoder for password hashing
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// imports Spring annotations for service layer, @Service marks it as a service component
import org.springframework.stereotype.Service;
// imports Spring's Transactional annotation for managing transactions ,Enables transactional behavior (commit / rollback)
import org.springframework.transaction.annotation.Transactional;

// imports Optional for handling potential null values
import java.util.Optional;

// Service implementation for managing User entities
@Service
// Lombok annotation to generate a constructor with required arguments (final fields)
@RequiredArgsConstructor
// Enables transactional behavior (commit / rollback)
@Transactional
// Implementation of UserService interface
public class UserServiceImpl implements UserService {
    // Repository for User entity
    private final UserRepository userRepository;
    // Password encoder for hashing passwords
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    // Creates a new user with validation and password hashing
    @Override
    public User createUser(User user) {
        // Basic validation
        userRepository.findByEmail(user.getEmail()).ifPresent(u -> {
            throw new IllegalArgumentException("Email already in use");
        });
        // Check for existing username
        userRepository.findByUsername(user.getUsername()).ifPresent(u -> {
            throw new IllegalArgumentException("Username already in use");
        });
        // Hash the password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
    // Finds a user by their ID, Transactional read-only for optimization. This method ONLY reads data. It will NOT modify the database. 
    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(Integer id) {
        return userRepository.findById(id);
    }
    // Finds a user by their username, Transactional read-only for optimization. This method ONLY reads data. It will NOT modify the database. 
    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    // Finds a user by their email, Transactional read-only for optimization. This method ONLY reads data. It will NOT modify the database.
    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
