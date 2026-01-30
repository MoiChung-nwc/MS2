package cd.studentservice.dto.response;

import cd.studentservice.enumerate.ClassStatus;
import cd.studentservice.mapper.SchoolMapper;
import org.mapstruct.Mapper;

import java.time.Instant;

public record ClassResponse(
        Long id,
        String code,
        String name,
        Integer grade,
        ClassStatus status,
        Instant createdAt,
        SchoolResponse school) {
}
