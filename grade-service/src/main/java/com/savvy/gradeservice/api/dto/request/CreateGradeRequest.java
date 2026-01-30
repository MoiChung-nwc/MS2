package com.savvy.gradeservice.api.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateGradeRequest {
    private Long schoolId;
    private Long classId;
    private Long studentId;
    private String term;
    private String subject;
    private BigDecimal score;
}
