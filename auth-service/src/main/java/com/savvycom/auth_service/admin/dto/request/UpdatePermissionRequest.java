package com.savvycom.auth_service.admin.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdatePermissionRequest {
    @NotBlank(message = "Permission code is required")
    private String code;
    private String description;
}
