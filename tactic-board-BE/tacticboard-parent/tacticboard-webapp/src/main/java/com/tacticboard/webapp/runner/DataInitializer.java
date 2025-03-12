package com.tacticboard.webapp.runner;

import com.tacticboard.core.model.entity.user.Role;
import com.tacticboard.core.model.entity.user.User;
import com.tacticboard.core.model.entity.user.Role.RoleType;
import com.tacticboard.core.util.Constants;
import com.tacticboard.persistence.repository.RoleRepository;
import com.tacticboard.persistence.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DataInitializer(RoleRepository roleRepository, UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        log.info("Initializing application data...");

        // Initialize roles if they don't exist
        initRoles();

        // Create admin user if it doesn't exist
        createAdminUserIfNotExists();

        log.info("Data initialization completed.");
    }

    private void initRoles() {
        if (roleRepository.count() == 0) {
            log.info("Creating default roles...");

            Role userRole = new Role();
            userRole.setName(RoleType.ROLE_USER);
            roleRepository.save(userRole);

            Role coachRole = new Role();
            coachRole.setName(RoleType.ROLE_COACH);
            roleRepository.save(coachRole);

            Role adminRole = new Role();
            adminRole.setName(RoleType.ROLE_ADMIN);
            roleRepository.save(adminRole);

            log.info("Default roles created.");
        }
    }

    private void createAdminUserIfNotExists() {
        if (!userRepository.existsByUsername("admin")) {
            log.info("Creating admin user...");

            User adminUser = new User();
            adminUser.setUsername("admin");
            adminUser.setEmail("admin@tacticboard.com");
            adminUser.setPassword(passwordEncoder.encode("admin123"));
            adminUser.setFirstName("Admin");
            adminUser.setLastName("User");
            adminUser.setActive(true);

            Role adminRole = roleRepository.findByName(RoleType.ROLE_ADMIN)
                    .orElseThrow(() -> new RuntimeException("Admin role not found"));

            Set<Role> roles = new HashSet<>();
            roles.add(adminRole);
            adminUser.setRoles(roles);

            userRepository.save(adminUser);

            log.info("Admin user created.");
        }
    }
}