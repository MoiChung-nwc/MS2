package cd.studentservice.dto.response;

import cd.studentservice.enumerate.SchoolStatus;

import java.time.Instant;

public record SchoolResponse(
        Long id,
        String code,
        String name,
        String address,
        SchoolStatus status,
        Instant createdAt) {
}
