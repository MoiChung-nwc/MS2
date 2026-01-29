package com.savvycom.auth_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="user_id", nullable=false)
    private Long userId;

    @Column(name="token_hash", nullable=false, unique=true, length=255)
    private String tokenHash;

    @Column(name="expires_at", nullable=false)
    private Instant expiresAt;

    @Column(name="revoked_at")
    private Instant revokedAt;

    @Column(name="replaced_by_token_hash", length=255)
    private String replacedByTokenHash;

    @Column(name="created_at", nullable=false)
    private Instant createdAt;

    public boolean isRevoked() {
        return revokedAt != null;
    }

    public boolean isExpired(Instant now) {
        return expiresAt.isBefore(now);
    }

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) this.createdAt = Instant.now();
    }
}
