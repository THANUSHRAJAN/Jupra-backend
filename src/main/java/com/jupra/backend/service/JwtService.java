package com.jupra.backend.service;

import com.jupra.backend.exception.ApiException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

/**
 * Issues and verifies JWTs for two purposes:
 *  - "access"  → the founder's signed-in session (used by /api/admin/**)
 *  - "reset"   → a short-lived token proving a verified OTP, used only by /api/auth/reset-password
 *
 * The configured secret is hashed with SHA-256 before use, so any non-empty secret
 * yields a valid 256-bit HMAC key regardless of its original length.
 */
@Service
public class JwtService {

    private final Key key;
    private final long accessMinutes;
    private final long resetMinutes;

    public JwtService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.access-token-minutes}") long accessMinutes,
            @Value("${app.jwt.reset-token-minutes}") long resetMinutes
    ) {
        this.key = Keys.hmacShaKeyFor(sha256(secret));
        this.accessMinutes = accessMinutes;
        this.resetMinutes = resetMinutes;
    }

    private static byte[] sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return digest.digest(value.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }

    public String generateAccessToken(String email) {
        return buildToken(email, "access", accessMinutes);
    }

    public String generateResetToken(String email) {
        return buildToken(email, "reset", resetMinutes);
    }

    public Date accessTokenExpiry() {
        return new Date(System.currentTimeMillis() + accessMinutes * 60_000);
    }

    private String buildToken(String subject, String purpose, long minutes) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + minutes * 60_000);
        return Jwts.builder()
                .setSubject(subject)
                .claim("purpose", purpose)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /** Validates a token's signature, expiry and expected purpose. Throws ApiException(401) otherwise. */
    public Claims validate(String token, String expectedPurpose) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            Object purpose = claims.get("purpose");
            if (!expectedPurpose.equals(purpose)) {
                throw new ApiException(401, "Invalid or expired session. Please sign in again.");
            }
            return claims;
        } catch (ExpiredJwtException e) {
            throw new ApiException(401, "Your session has expired. Please sign in again.");
        } catch (JwtException | IllegalArgumentException e) {
            throw new ApiException(401, "Invalid or expired session. Please sign in again.");
        }
    }
}
