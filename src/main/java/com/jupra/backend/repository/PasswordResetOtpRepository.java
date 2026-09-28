package com.jupra.backend.repository;

import com.jupra.backend.domain.PasswordResetOtp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PasswordResetOtpRepository extends JpaRepository<PasswordResetOtp, Long> {
    List<PasswordResetOtp> findByEmailIgnoreCaseAndUsedFalseOrderByCreatedAtDesc(String email);
}
