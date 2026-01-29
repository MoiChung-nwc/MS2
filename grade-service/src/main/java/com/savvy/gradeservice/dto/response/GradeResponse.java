package com.savvy.gradeservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GradeResponse {
    private Long id;
    private Long schoolId;
    private Long classId;
    private Long studentId;
    private String term;
    private String subject;
    private BigDecimal score;
}
