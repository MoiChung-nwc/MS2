package com.savvycom.auth_service.admin.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdminSetUserSchoolScopeRequest {
    @NotEmpty(message = "schoolIds is required")
    private List<Long> schoolIds;
}
