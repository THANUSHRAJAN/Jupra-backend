package com.jupra.backend.service;

import com.jupra.backend.domain.AdminUser;
import com.jupra.backend.domain.PasswordResetOtp;
import com.jupra.backend.dto.LoginResponse;
import com.jupra.backend.dto.VerifyOtpResponse;
import com.jupra.backend.exception.ApiException;
import com.jupra.backend.repository.AdminUserRepository;
import com.jupra.backend.repository.PasswordResetOtpRepository;
import io.jsonwebtoken.Claims;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

/**
 * Handles founder sign-in and the "forgot password" flow:
 *   1) forgotPassword(email)  → emails a 6-digit OTP, valid for 10 minutes
 *   2) verifyOtp(email, otp)  → marks the OTP used, returns a short-lived "reset" JWT
 *   3) resetPassword(token,…) → verifies that JWT and updates the password
 */
@Service
public class AuthService {

    private static final SecureRandom RANDOM = new SecureRandom();

    private final AdminUserRepository adminUserRepository;
    private final PasswordResetOtpRepository otpRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final MailService mailService;

    public AuthService(AdminUserRepository adminUserRepository,
                        PasswordResetOtpRepository otpRepository,
                        PasswordEncoder passwordEncoder,
                        JwtService jwtService,
                        MailService mailService) {
        this.adminUserRepository = adminUserRepository;
        this.otpRepository = otpRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.mailService = mailService;
    }

    public LoginResponse login(String email, String password) {
        AdminUser admin = adminUserRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ApiException(401, "Invalid email or password."));

        if (!passwordEncoder.matches(password, admin.getPasswordHash())) {
            throw new ApiException(401, "Invalid email or password.");
        }

        String token = jwtService.generateAccessToken(admin.getEmail());
        return new LoginResponse(token, admin.getEmail(), jwtService.accessTokenExpiry().toInstant());
    }

    @Transactional
    public void forgotPassword(String email) {
        Optional<AdminUser> adminOpt = adminUserRepository.findByEmailIgnoreCase(email);
        // Always behave the same way whether or not the email exists, to avoid leaking who has an account.
        if (adminOpt.isPresent()) {
            String otp = generateOtp();
            PasswordResetOtp record = new PasswordResetOtp();
            record.setEmail(email.toLowerCase());
            record.setOtpHash(passwordEncoder.encode(otp));
            record.setExpiresAt(Instant.now().plus(10, ChronoUnit.MINUTES));
            record.setUsed(false);
            otpRepository.save(record);
            mailService.sendOtpEmail(adminOpt.get().getEmail(), otp);
        }
    }

    @Transactional
    public VerifyOtpResponse verifyOtp(String email, String otp) {
        List<PasswordResetOtp> candidates =
                otpRepository.findByEmailIgnoreCaseAndUsedFalseOrderByCreatedAtDesc(email);

        Instant now = Instant.now();
        for (PasswordResetOtp record : candidates) {
            if (record.getExpiresAt().isAfter(now) && passwordEncoder.matches(otp, record.getOtpHash())) {
                record.setUsed(true);
                otpRepository.save(record);
                String resetToken = jwtService.generateResetToken(email.toLowerCase());
                return new VerifyOtpResponse(resetToken);
            }
        }
        throw new ApiException(400, "That code is invalid or has expired. Please request a new one.");
    }

    @Transactional
    public void resetPassword(String resetToken, String newPassword) {
        Claims claims = jwtService.validate(resetToken, "reset");
        String email = claims.getSubject();

        AdminUser admin = adminUserRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ApiException(404, "Account not found."));

        admin.setPasswordHash(passwordEncoder.encode(newPassword));
        adminUserRepository.save(admin);
    }

    private String generateOtp() {
        int code = RANDOM.nextInt(1_000_000);
        return String.format("%06d", code);
    }
}
