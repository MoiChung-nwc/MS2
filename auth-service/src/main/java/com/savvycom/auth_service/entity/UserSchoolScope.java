package com.savvycom.auth_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "user_school_scope")
@IdClass(UserSchoolScope.PK.class)
public class UserSchoolScope {

    @Id
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Id
    @Column(name = "school_id", nullable = false)
    private Long schoolId;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class PK implements Serializable {
        private Long userId;
        private Long schoolId;
    }
}
