package com.savvycom.auth_service.admin.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdminSetUserStudentRequest {
    @NotNull(message = "studentId is required")
    private Long studentId;
}
