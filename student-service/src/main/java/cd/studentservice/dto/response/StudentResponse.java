package cd.studentservice.dto.response;

import cd.studentservice.enumerate.StudentStatus;

import java.time.Instant;
import java.time.LocalDate;

public record StudentResponse(
        Long id,
        String code,
        String fullName,
        LocalDate dob,
        Boolean gender,
        StudentStatus status,
        Instant createdAt,
        ClassResponse clazz) {
}
