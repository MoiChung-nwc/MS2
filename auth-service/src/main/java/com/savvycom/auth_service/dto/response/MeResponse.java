package com.savvycom.auth_service.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeResponse {
    private Long userId;
    private String username;
    private String email;
    private List<String> roles;
    private List<String> permissions;
    private DataScope dataScope;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DataScope {
        private List<Long> schoolIds;
    }
}
