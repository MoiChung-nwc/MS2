package com.savvycom.auth_service.service;

import com.savvycom.auth_service.entity.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.*;

@Service
public class JwtService {

    private final SecretKey key;
    private final long accessExpMs;

    public JwtService(
            @Value("${application.security.jwt.secret-key}") String secret,
            @Value("${application.security.jwt.expiration}") long accessExpMs
    ) {
        this.key = Keys.hmacShaKeyFor(decodeSecret(secret));
        this.accessExpMs = accessExpMs;
    }

    // roles: ["ADMIN", "STUDENT"]
    // permissions: ["GRADE_WRITE", "SCHOOL_READ"]
    // schoolIds: [1,2,3]
    // studentId: 1001 (nullable)
    public String generateAccessToken(
            User user,
            List<String> roles,
            List<String> permissions,
            List<Long> schoolIds,
            Long studentId
    ) {
        Instant now = Instant.now();
        Date issuedAt = Date.from(now);
        Date exp = Date.from(now.plusMillis(accessExpMs));

        Map<String, Object> dataScope = new HashMap<>();
        dataScope.put("schoolIds", schoolIds == null ? List.of() : schoolIds);

        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", roles == null ? List.of() : roles);
        claims.put("permissions", permissions == null ? List.of() : permissions);
        claims.put("dataScope", dataScope);

        if (studentId != null) {
            claims.put("studentId", studentId);
        }

        return Jwts.builder()
                .setSubject(String.valueOf(user.getId())) // sub = userId
                .setIssuedAt(issuedAt)
                .setExpiration(exp)
                .addClaims(claims)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    private static byte[] decodeSecret(String s) {
        if (s == null) throw new IllegalArgumentException("JWT secret is null");
        String t = s.trim();

        // hex
        if (t.matches("^[0-9a-fA-F]+$") && t.length() % 2 == 0) {
            byte[] out = new byte[t.length() / 2];
            for (int i = 0; i < out.length; i++) {
                int hi = Character.digit(t.charAt(i * 2), 16);
                int lo = Character.digit(t.charAt(i * 2 + 1), 16);
                out[i] = (byte) ((hi << 4) + lo);
            }
            return out;
        }

        // base64
        return Base64.getDecoder().decode(t);
    }
}
