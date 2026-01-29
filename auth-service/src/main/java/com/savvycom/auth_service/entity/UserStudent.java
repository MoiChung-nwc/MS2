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
@Table(
        name = "user_student",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "user_id"),
                @UniqueConstraint(columnNames = "student_id")
        }
)
public class UserStudent {

    @Id
    @Column(name="user_id", nullable=false)
    private Long userId;

    @Column(name="student_id", nullable=false)
    private Long studentId;

    @Column(name="created_at", nullable=false)
    private Instant createdAt;

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) this.createdAt = Instant.now();
    }
}
