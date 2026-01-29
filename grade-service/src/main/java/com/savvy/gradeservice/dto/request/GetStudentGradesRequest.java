package com.savvy.gradeservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetStudentGradesRequest {
    
    @NotNull(message = "studentId is required")
    private Long studentId;
    
    @NotBlank(message = "term is required")
    private String term;
}
