// declares the package for the UserMapper class
package com.example.accounting.mapper;

// imports UserDto and User entity for mapping
import com.example.accounting.dto.UserDto;
import com.example.accounting.entity.User;

// Utility class for mapping User entity to UserDto
public final class UserMapper {
    private UserMapper() {}
    // Converts a User entity to a UserDto
    public static UserDto toDto(User user) {
        // Return null if user is null, else create and return UserDto
        if (user == null) return null;
        // Map fields from User entity to UserDto
        return new UserDto(user.getId(), user.getUsername(), user.getEmail());
    }
}
