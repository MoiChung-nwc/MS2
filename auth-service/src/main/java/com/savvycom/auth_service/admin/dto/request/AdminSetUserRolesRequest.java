package com.savvycom.auth_service.admin.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdminSetUserRolesRequest {
    @NotEmpty(message = "roleNames is required")
    private List<String> roleNames;
}
