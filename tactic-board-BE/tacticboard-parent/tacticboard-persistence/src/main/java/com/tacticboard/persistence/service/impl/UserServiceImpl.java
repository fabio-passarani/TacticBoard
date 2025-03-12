package com.tacticboard.persistence.service.impl;

import com.tacticboard.core.exception.ResourceNotFoundException;
import com.tacticboard.core.model.entity.user.User;
import com.tacticboard.core.model.entity.user.Role;
import com.tacticboard.core.model.entity.user.Subscription;
import com.tacticboard.core.service.UserService;
import com.tacticboard.persistence.repository.RoleRepository;
import com.tacticboard.persistence.repository.SubscriptionRepository;
import com.tacticboard.persistence.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(
            UserRepository userRepository,
            RoleRepository roleRepository,
            SubscriptionRepository subscriptionRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User createUser(User user) {
        // Check if username or email already exists
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalStateException("Username already exists");
        }
        
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalStateException("Email already exists");
        }
        
        // Encode password
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // Set default role if not provided
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            Set<Role> roles = new HashSet<>();
            Role userRole = roleRepository.findByName("ROLE_USER")
                    .orElseThrow(() -> new IllegalStateException("Default role not found"));
            roles.add(userRole);
            user.setRoles(roles);
        }
        
        user.setCreatedAt(LocalDateTime.now());
        user.setActive(true);
        
        return userRepository.save(user);
    }

    @Override
    public User updateUser(User user) {
        User existingUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", user.getId()));
        
        // Check if username is being changed and if it already exists
        if (!existingUser.getUsername().equals(user.getUsername()) && 
                userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalStateException("Username already exists");
        }
        
        // Check if email is being changed and if it already exists
        if (!existingUser.getEmail().equals(user.getEmail()) && 
                userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalStateException("Email already exists");
        }
        
        // Don't update password if it's empty or null
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            user.setPassword(existingUser.getPassword());
        } else {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        
        user.setUpdatedAt(LocalDateTime.now());
        
        return userRepository.save(user);
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User", "id", id);
        }
        
        userRepository.deleteById(id);
    }

    @Override
    public void deactivateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        
        user.setActive(false);
        user.setUpdatedAt(LocalDateTime.now());
        
        userRepository.save(user);
    }

    @Override
    public void activateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        
        user.setActive(true);
        user.setUpdatedAt(LocalDateTime.now());
        
        userRepository.save(user);
    }

    @Override
    public void changePassword(Long id, String oldPassword, String newPassword) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        
        // Verify old password
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalStateException("Current password is incorrect");
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        
        userRepository.save(user);
    }

    @Override
    public void resetPassword(Long id, String newPassword) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        
        userRepository.save(user);
    }

    @Override
    public void addRoleToUser(Long userId, String roleName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "name", roleName));
        
        user.getRoles().add(role);
        userRepository.save(user);
    }

    @Override
    public void removeRoleFromUser(Long userId, String roleName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "name", roleName));
        
        user.getRoles().remove(role);
        userRepository.save(user);
    }

    @Override
    public Subscription createSubscription(Subscription subscription, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        // Deactivate any existing active subscriptions
        List<Subscription> activeSubscriptions = subscriptionRepository.findByUserIdAndIsActiveTrue(userId);
        for (Subscription active : activeSubscriptions) {
            active.setActive(false);
            active.setEndedAt(LocalDateTime.now());
            subscriptionRepository.save(active);
        }
        
        subscription.setUser(user);
        subscription.setStartDate(LocalDateTime.now());
        subscription.setActive(true);
        
        return subscriptionRepository.save(subscription);
    }

    @Override
    public Optional<Subscription> getActiveSubscription(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }
        
        return subscriptionRepository.findByUserIdAndIsActiveTrue(userId).stream().findFirst();
    }

    @Override
    public List<Subscription> getUserSubscriptions(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }
        
        return subscriptionRepository.findByUserId(userId);
    }

    @Override
    public void cancelSubscription(Long subscriptionId) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription", "id", subscriptionId));
        
        subscription.setActive(false);
        subscription.setEndedAt(LocalDateTime.now());
        
        subscriptionRepository.save(subscription);
    }

    @Override
    public List<User> searchUsers(String query) {
        return userRepository.findByUsernameContainingOrEmailContainingOrNameContaining(query, query, query);
    }
}