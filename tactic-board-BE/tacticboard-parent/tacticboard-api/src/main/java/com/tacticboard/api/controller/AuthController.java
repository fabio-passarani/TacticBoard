package com.tacticboard.api.controller;

import com.tacticboard.api.payload.request.LoginRequest;
import com.tacticboard.api.payload.request.RefreshTokenRequest;
import com.tacticboard.api.payload.request.SignupRequest;
import com.tacticboard.api.payload.response.JwtAuthenticationResponse;
import com.tacticboard.api.payload.response.MessageResponse;
import com.tacticboard.api.util.ResponseBuilder;
import com.tacticboard.core.model.entity.user.Role;
import com.tacticboard.core.model.entity.user.User;
import com.tacticboard.core.model.entity.user.Role.RoleType;
import com.tacticboard.core.service.UserService;
import com.tacticboard.security.jwt.JwtTokenProvider;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtTokenProvider tokenProvider;

    @Autowired
    public AuthController(
            AuthenticationManager authenticationManager,
            UserService userService,
            JwtTokenProvider tokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.tokenProvider = tokenProvider;
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsernameOrEmail(),
                        loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = tokenProvider.generateToken(authentication);
        String refreshToken = tokenProvider.generateRefreshToken(authentication);

        return ResponseBuilder.success(new JwtAuthenticationResponse(accessToken, refreshToken));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        // Validate refresh token
        if (!tokenProvider.validateToken(refreshTokenRequest.getRefreshToken())) {
            return ResponseBuilder.error(org.springframework.http.HttpStatus.BAD_REQUEST, "Invalid refresh token",
                    null);
        }

        // Get username from refresh token
        String username = tokenProvider.getUsernameFromJWT(refreshTokenRequest.getRefreshToken());

        // Create a new authentication object
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        username,
                        refreshTokenRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Generate new tokens
        String accessToken = tokenProvider.generateToken(authentication);
        String refreshToken = tokenProvider.generateRefreshToken(authentication);

        return ResponseBuilder.success(new JwtAuthenticationResponse(accessToken, refreshToken));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        // Check if username is already taken
        if (userService.getUserByUsername(signUpRequest.getUsername()).isPresent()) {
            return ResponseBuilder.error(org.springframework.http.HttpStatus.BAD_REQUEST, "Username is already taken!",
                    null);
        }

        // Check if email is already in use
        if (userService.getUserByEmail(signUpRequest.getEmail()).isPresent()) {
            return ResponseBuilder.error(org.springframework.http.HttpStatus.BAD_REQUEST, "Email is already in use!",
                    null);
        }

        // Create new user's account
        User user = new User();
        // user.setName(signUpRequest.getName());
        user.setUsername(signUpRequest.getUsername());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(signUpRequest.getPassword());

        Set<Role> roles = new HashSet<>();
        Role userRole = new Role();
        userRole.setName(RoleType.ROLE_USER);
        roles.add(userRole);
        user.setRoles(roles);

        userService.createUser(user);

        return ResponseBuilder.created(new MessageResponse("User registered successfully!"));
    }
}