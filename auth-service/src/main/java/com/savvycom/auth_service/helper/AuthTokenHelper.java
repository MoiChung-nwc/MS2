package com.savvycom.auth_service.helper;

import com.savvy.common.exception.BusinessException;
import com.savvy.common.exception.ErrorCode;
import com.savvycom.auth_service.entity.RefreshToken;
import com.savvycom.auth_service.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AuthTokenHelper {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshTokenExpirationMs;

    @Transactional
    public String issueRefreshToken(Long userId) {
        Instant now = Instant.now();

        String raw = UUID.randomUUID().toString();
        String hash = sha256(raw);

        refreshTokenRepository.save(RefreshToken.builder()
                .userId(userId)
                .tokenHash(hash)
                .expiresAt(now.plusMillis(refreshTokenExpirationMs))
                .createdAt(now)
                .build());

        return raw;
    }

    @Transactional(readOnly = true)
    public RefreshToken getRefreshTokenOrThrow(String rawToken) {
        if (rawToken == null || rawToken.isBlank()) {
            throw new BusinessException(ErrorCode.TOKEN_INVALID, "refresh token is missing");
        }
        String hash = sha256(rawToken);
        return refreshTokenRepository.findByTokenHash(hash)
                .orElseThrow(() -> new BusinessException(ErrorCode.TOKEN_INVALID, "refresh token revoked or not found"));
    }

    @Transactional(readOnly = true)
    public RefreshToken getUsableRefreshTokenOrThrow(String rawToken) {
        RefreshToken rt = getRefreshTokenOrThrow(rawToken);
        assertUsable(rt);
        return rt;
    }

    public void assertUsable(RefreshToken token) {
        Instant now = Instant.now();

        if (token.getRevokedAt() != null) {
            throw new BusinessException(ErrorCode.TOKEN_INVALID, "refresh token revoked or not found");
        }
        if (token.getExpiresAt() == null || token.getExpiresAt().isBefore(now)) {
            throw new BusinessException(ErrorCode.TOKEN_EXPIRED, "refresh token expired");
        }
    }

    @Transactional
    public String rotate(RefreshToken oldToken) {
        assertUsable(oldToken);

        Instant now = Instant.now();

        String newRaw = UUID.randomUUID().toString();
        String newHash = sha256(newRaw);

        refreshTokenRepository.save(RefreshToken.builder()
                .userId(oldToken.getUserId())
                .tokenHash(newHash)
                .expiresAt(now.plusMillis(refreshTokenExpirationMs))
                .createdAt(now)
                .build());

        oldToken.setRevokedAt(now);
        oldToken.setReplacedByTokenHash(newHash);
        refreshTokenRepository.save(oldToken);

        return newRaw;
    }

    @Transactional
    public void revokeIfNeeded(RefreshToken token) {
        if (token.getRevokedAt() == null) {
            token.setRevokedAt(Instant.now());
            refreshTokenRepository.save(token);
        }
    }

    private String sha256(String raw) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(raw.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "Cannot hash token", e);
        }
    }
}
