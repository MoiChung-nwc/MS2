package com.savvycom.auth_service.admin.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateRoleRequest {
    @NotEmpty(message = "Role name is required")
    private String name;
    private String description;
    private List<String> permissionCodes;
}
