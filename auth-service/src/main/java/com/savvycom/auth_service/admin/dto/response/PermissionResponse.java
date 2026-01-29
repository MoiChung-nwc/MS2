package com.savvycom.auth_service.admin.dto.response;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PermissionResponse {
    private Long id;
    private String code;
    private String description;
}
