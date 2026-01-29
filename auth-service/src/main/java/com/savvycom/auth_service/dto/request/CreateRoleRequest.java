package com.savvycom.auth_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateRoleRequest {
    @NotBlank
    private String name;
    private String description;
    private List<String> permissionCodes;
}
