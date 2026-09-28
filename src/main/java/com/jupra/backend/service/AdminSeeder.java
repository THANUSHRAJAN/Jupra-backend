package com.jupra.backend.service;

import com.jupra.backend.domain.AdminUser;
import com.jupra.backend.repository.AdminUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

/**
 * Creates the first founder account on startup if none exists yet.
 * If ADMIN_PASSWORD is not set, a random password is generated and printed once to the
 * application logs — sign in with it, then use "Forgot password" to set a password you'll remember.
 */
@Component
public class AdminSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminSeeder.class);
    private static final String ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    private final AdminUserRepository adminUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.seed-email}")
    private String seedEmail;

    @Value("${app.admin.seed-password:}")
    private String seedPassword;

    public AdminSeeder(AdminUserRepository adminUserRepository, PasswordEncoder passwordEncoder) {
        this.adminUserRepository = adminUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (adminUserRepository.count() > 0) {
            return;
        }

        String password = (seedPassword == null || seedPassword.isBlank())
                ? generateRandomPassword(12)
                : seedPassword;

        AdminUser admin = new AdminUser();
        admin.setEmail(seedEmail.toLowerCase());
        admin.setPasswordHash(passwordEncoder.encode(password));
        adminUserRepository.save(admin);

        log.warn("=========================================================");
        log.warn(" JUPRA founder account created");
        log.warn(" Email:    {}", admin.getEmail());
        log.warn(" Password: {}", password);
        log.warn(" Sign in, then use 'Forgot password' to set your own password.");
        log.warn("=========================================================");
    }

    private String generateRandomPassword(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }
}
