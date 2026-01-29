package com.savvycom.auth_service.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserStudentIdResponse {
    private Long userId;
    private Long studentId;
}
