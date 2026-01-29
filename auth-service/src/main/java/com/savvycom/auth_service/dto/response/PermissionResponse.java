package com.savvycom.auth_service.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PermissionResponse {
    private Long id;
    private String code;
    private String description;
}
