package com.savvy.gradeservice.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentGradesResponse {
    private Long studentId;
    private String term;
    private List<GradeItemResponse> items;
}
