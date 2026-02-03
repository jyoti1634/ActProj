package com.example.accounting.controller;

// 
import com.example.accounting.dto.*;
// imports user entity for authentication processes
import com.example.accounting.entity.User;
// imports UserMapper for converting User entities to DTOs
import com.example.accounting.mapper.UserMapper;
// imports JWT utilities for token generation
import com.example.accounting.security.JwtUtils;
// imports UserService for user management operations
import com.example.accounting.service.UserService;
// Lombok annotation for constructor injection
import lombok.RequiredArgsConstructor;
// imports ResponseEntity for HTTP responses
import org.springframework.http.ResponseEntity;
// @Autowired is used for Dependency Injection. It tells Spring: "I need an object of this type. Please find it in your 'Bucket of Beans' and plug it in here."
import org.springframework.beans.factory.annotation.Autowired;
// @Value is used to inject values from external configuration files (like application.properties or application.yml) or environment variables.
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
// imports Spring's validation annotation
import org.springframework.validation.annotation.Validated;
// imports Spring's REST controller annotations
import org.springframework.web.bind.annotation.*;

// imports Jakarta Validation for request body validation
import jakarta.validation.Valid;
// imports Optional for handling potential null values
import java.util.Optional;

// Defines this class as a REST controller with base URL /api/v1/auth
@RestController
// request mapping for authentication-related endpoints
@RequestMapping("/api/v1/auth")
// Lombok annotation to generate a constructor with required arguments (final fields)
@RequiredArgsConstructor
// Enables Spring's validation for method parameters
@Validated
// Controller class for authentication operations
public class AuthController {

    // Service for user management
    private final UserService userService;
    // Utility for JWT token operations (optional when security disabled)
    @Autowired(required = false)
    private JwtUtils jwtUtils;

    // Password encoder bean (always available)
    @Autowired
    private PasswordEncoder passwordEncoder;

    // JWT expiration time from application properties (default to 1 hour)
    @Value("${app.security.jwtExpirationMs:3600000}")
    private long jwtExpirationMs;
    // Endpoint for user registration
    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@Valid @RequestBody RegisterRequest request) {
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(request.getPassword())
                .build();
        User saved = userService.createUser(user);
        return ResponseEntity.ok(UserMapper.toDto(saved));
    }

    // Endpoint for user login
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        Optional<User> userOpt = userService.findByUsername(request.getUsernameOrEmail());
        if (userOpt.isEmpty()) userOpt = userService.findByEmail(request.getUsernameOrEmail());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).body(new ErrorResponse("user_not_found", "No user with given usernameOrEmail"));
        }
        User user = userOpt.get();
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(401).body(new ErrorResponse("invalid_password", "Password does not match"));
        }
        String token = null;
        long expires = 0L;
        if (jwtUtils != null) {
            token = jwtUtils.generateToken(String.valueOf(user.getId()));
            expires = jwtExpirationMs;
        }
        AuthResponse resp = new AuthResponse(token, expires, UserMapper.toDto(user));
        return ResponseEntity.ok(resp);
    }
}
