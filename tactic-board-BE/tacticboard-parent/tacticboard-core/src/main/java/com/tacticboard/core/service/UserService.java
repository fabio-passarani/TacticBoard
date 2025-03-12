package com.tacticboard.core.service;

import com.tacticboard.core.model.entity.user.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    
    User createUser(User user);
    
    User updateUser(User user);
    
    Optional<User> getUserById(Long id);
    
    Optional<User> getUserByUsername(String username);
    
    Optional<User> getUserByEmail(String email);
    
    List<User> getAllUsers();
    
    void deleteUser(Long id);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
}