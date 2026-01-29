package com.savvy.gradeservice.dto.request;

import jakarta.validation.constraints.NotBlank;
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
    @NotNull(message = "schoolId is required")
    private Long schoolId;
    
    @NotNull(message = "classId is required")
    private Long classId;
    
    @NotNull(message = "studentId is required")
    private Long studentId;
    
    @NotBlank(message = "term is required")
    private String term;
    
    @NotBlank(message = "subject is required")
    private String subject;
    
    @NotNull(message = "score is required")
    private BigDecimal score;
}
