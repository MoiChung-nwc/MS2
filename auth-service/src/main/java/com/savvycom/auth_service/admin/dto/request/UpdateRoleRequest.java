package com.savvycom.auth_service.admin.dto.request;

import lombok.*;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateRoleRequest {
    private String description;
    private List<String> permissionCodes;
}
