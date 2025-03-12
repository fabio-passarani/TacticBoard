package com.tacticboard.api.controller;

import com.tacticboard.api.payload.request.ChangePasswordRequest;
import com.tacticboard.api.payload.request.UpdateUserRequest;
import com.tacticboard.api.util.ResponseBuilder;
import com.tacticboard.core.exception.ResourceNotFoundException;
import com.tacticboard.core.model.entity.user.User;
import com.tacticboard.core.service.UserService;
import com.tacticboard.security.util.SecurityUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        String username = SecurityUtils.getCurrentUsername()
                .orElseThrow(() -> new IllegalStateException("No authenticated user found"));
        
        User user = userService.getUserByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        return ResponseBuilder.success(user);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isCurrentUser(#id)")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        
        return ResponseBuilder.success(user);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseBuilder.success(users);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isCurrentUser(#id)")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest updateUserRequest) {
        User user = userService.getUserById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        
        user.setName(updateUserRequest.getName());
        user.setEmail(updateUserRequest.getEmail());
        user.setUsername(updateUserRequest.getUsername());
        
        User updatedUser = userService.updateUser(user);
        return ResponseBuilder.success("User updated successfully", updatedUser);
    }

    @PatchMapping("/{id}/password")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isCurrentUser(#id)")
    public ResponseEntity<?> changePassword(@PathVariable Long id, @Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
        userService.changePassword(id, changePasswordRequest.getCurrentPassword(), changePasswordRequest.getNewPassword());
        return ResponseBuilder.success("Password changed successfully", null);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isCurrentUser(#id)")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseBuilder.success("User deleted successfully", null);
    }
}