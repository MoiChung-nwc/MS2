package com.savvycom.auth_service.admin.dto.response;

import lombok.*;

import java.time.Instant;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminUserDetailResponse {
    private Long id;
    private String email;
    private String username;
    private boolean enabled;
    private List<String> roles;
    private List<String> permissions;
    private List<Long> schoolIds;
    private Long studentId;
    private Instant createdAt;
    private Instant updatedAt;
}
