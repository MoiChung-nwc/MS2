package com.savvycom.auth_service.dto.request;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateRolePermissionsRequest {
    private List<String> permissionCodes;
}
