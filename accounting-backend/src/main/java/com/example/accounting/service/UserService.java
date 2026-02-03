// declares the package for the UserService interface
package com.example.accounting.service;
// imports the User entity to be used in the service methods
import com.example.accounting.entity.User;
// imports Optional for handling potential null values
import java.util.Optional;
// Service interface for managing User entities
public interface UserService {
    // Method to create a new user
    User createUser(User user);
    // Method to find a user by their ID
    Optional<User> findById(Integer id);
    // Method to find a user by their username
    Optional<User> findByUsername(String username);
    // Method to find a user by their email
    Optional<User> findByEmail(String email);
}
