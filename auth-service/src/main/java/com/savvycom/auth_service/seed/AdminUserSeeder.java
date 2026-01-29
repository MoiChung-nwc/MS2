package com.savvycom.auth_service.seed;

import com.savvycom.auth_service.entity.Role;
import com.savvycom.auth_service.entity.User;
import com.savvycom.auth_service.repository.RoleRepository;
import com.savvycom.auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Component
@RequiredArgsConstructor
@Order(2)
public class AdminUserSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.seed.admin.email:admin@gmail.com}")
    private String adminEmail;

    @Value("${app.seed.admin.password:admin123}")
    private String adminPassword;

    @Value("${app.seed.admin.username:admin}")
    private String adminUsername;

    @Override
    @Transactional
    public void run(String... args) {
        String email = adminEmail.trim().toLowerCase();
        if (userRepository.existsByEmail(email)) return;

        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseThrow(() -> new IllegalStateException("Role ADMIN not found. RBAC seed not run?"));

        Instant now = Instant.now();

        User admin = User.builder()
                .email(email)
                .username(adminUsername)
                .passwordHash(passwordEncoder.encode(adminPassword))
                .enabled(true)
                .createdAt(now)
                .updatedAt(now)
                .build();

        admin.getRoles().add(adminRole);
        userRepository.save(admin);
    }
}
