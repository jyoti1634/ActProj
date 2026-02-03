package com.example.accounting.controller;

import com.example.accounting.dto.UserDto;
import com.example.accounting.entity.User;
import com.example.accounting.mapper.UserMapper;
import com.example.accounting.security.UserPrincipal;
import com.example.accounting.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserDto> me() {
        // The SecurityContextHolder contains a SecurityContext object. This object holds the Authentication object, which contains the details of the currently authenticated user.
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // auth.getPrincipal() is the object representing the currently logged-in user.Often it is an instance of UserDetails or your custom UserPrincipal class.instanceof UserPrincipal checks if the user object is of type UserPrincipal.
        if (auth == null || !(auth.getPrincipal() instanceof UserPrincipal)) {
            // 401 Unauthorized is an HTTP status meaning the request requires authentication or failed authentication
            return ResponseEntity.status(401).build();
        }
        // you assign the cast object to a variable principal of type UserPrincipal
        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        User user = userService.findById(principal.getId()).orElseThrow();
        // ResponseEntity.ok() is a static method that creates a ResponseEntity with the given body and an HTTP status of 200 OK. UserMapper.toDto(user) converts the User entity to a UserDto, which is a data transfer object used for sending user data in the response.
        return ResponseEntity.ok(UserMapper.toDto(user));
    }
}
