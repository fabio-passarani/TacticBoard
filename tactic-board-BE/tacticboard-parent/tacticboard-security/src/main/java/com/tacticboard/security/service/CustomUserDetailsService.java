package com.tacticboard.security.service;

import com.tacticboard.core.model.entity.user.User;
import com.tacticboard.core.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserService userService;

    @Autowired
    public CustomUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        // Let users login with either username or email
        User user = userService.getUserByUsername(usernameOrEmail)
                .orElseGet(() -> userService.getUserByEmail(usernameOrEmail)
                        .orElseThrow(() -> new UsernameNotFoundException(
                                "User not found with username or email: " + usernameOrEmail)));

        if (!user.isActive()) {
            throw new UsernameNotFoundException("User is deactivated: " + usernameOrEmail);
        }

        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.isActive(),
                true,
                true,
                true,
                authorities);
    }

    @Transactional
    public UserDetails loadUserById(Long id) {
        User user = userService.getUserById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));

        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.isActive(),
                true,
                true,
                true,
                authorities);
    }
}