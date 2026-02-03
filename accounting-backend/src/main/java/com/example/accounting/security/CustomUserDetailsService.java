package com.example.accounting.security;

import com.example.accounting.entity.User;
import com.example.accounting.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(prefix = "app.security", name = "enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.findByUsername(username)
                .orElseGet(() -> userService.findByEmail(username).orElse(null));
        if (user == null) throw new UsernameNotFoundException("User not found: " + username);
        return UserPrincipal.create(user);
    }

    public UserDetails loadUserById(Integer id) {
        User user = userService.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));
        return UserPrincipal.create(user);
    }
}
