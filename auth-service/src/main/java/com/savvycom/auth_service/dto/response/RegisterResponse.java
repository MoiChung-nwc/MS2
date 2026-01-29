package com.savvycom.auth_service.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterResponse {

    private Long id;
    private String username;
    private String email;
    private List<String> roles;
    private DataScope dataScope;
    private Long studentId;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DataScope {
        private List<Long> schoolIds;
    }
}
