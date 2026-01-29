package com.savvycom.auth_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatePermissionRequest {
    @NotBlank
    private String code;

    private String description;
}
