package com.savvycom.auth_service.admin.dto.request;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdminUpdateUserRequest {
    private String username;
    private Boolean enabled;
}
