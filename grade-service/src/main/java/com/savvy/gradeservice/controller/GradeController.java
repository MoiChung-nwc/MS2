package com.savvy.gradeservice.controller;

import com.savvy.common.dto.BaseResponse;
import com.savvy.gradeservice.dto.request.CreateGradeRequest;
import com.savvy.gradeservice.dto.request.GetStudentGradesRequest;
import com.savvy.gradeservice.dto.response.GradeResponse;
import com.savvy.gradeservice.dto.response.StudentGradesResponse;
import com.savvy.gradeservice.service.GradeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/grades")
@RequiredArgsConstructor
public class GradeController {

    private final GradeService gradeService;

    @GetMapping
    public BaseResponse<StudentGradesResponse> getStudentGrades(
            @RequestParam Long studentId,
            @RequestParam String term
    ) {
        StudentGradesResponse response = gradeService.getStudentGrades(studentId, term);
        return BaseResponse.success(response);
    }

    @PostMapping
    public BaseResponse<GradeResponse> createGrade(@Valid @RequestBody CreateGradeRequest request) {
        GradeResponse response = gradeService.createGrade(request);
        return BaseResponse.created(response, "Grade created");
    }
}
