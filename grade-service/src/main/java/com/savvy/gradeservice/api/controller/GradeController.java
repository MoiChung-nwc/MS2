package com.savvy.gradeservice.api.controller;

import com.savvy.common.dto.BaseResponse;
import com.savvy.gradeservice.api.dto.request.CreateGradeRequest;
import com.savvy.gradeservice.api.dto.request.UpdateGradeRequest;
import com.savvy.gradeservice.api.dto.response.GradeResponse;
import com.savvy.gradeservice.api.dto.response.StudentGradesResponse;
import com.savvy.gradeservice.service.IGradeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/grades")
@RequiredArgsConstructor
public class GradeController {
    private final IGradeService gradeService;

    @GetMapping
    public BaseResponse<StudentGradesResponse> getStudentGrades(
            @RequestParam(required = false) Long studentId,
            @RequestParam String term,
            @RequestParam Long schoolId
    ) {
        StudentGradesResponse response = gradeService.getStudentGrades(studentId, term, schoolId);
        return BaseResponse.success(response);
    }

    @GetMapping("/all")
    public BaseResponse<List<GradeResponse>> getAllGrades() {
        List<GradeResponse> response = gradeService.getAllGrades();
        return BaseResponse.success(response);
    }

    @PostMapping
    public BaseResponse<GradeResponse> createGrade(@Valid @RequestBody CreateGradeRequest request) {
        GradeResponse response = gradeService.createGrade(request);
        return BaseResponse.created(response, "Grade created");
    }

    @PutMapping("/{gradeId}")
    public BaseResponse<GradeResponse> updateGrade(
            @PathVariable Long gradeId,
            @Valid @RequestBody UpdateGradeRequest request
    ) {
        GradeResponse response = gradeService.updateGrade(gradeId, request);
        return BaseResponse.success(response);
    }
}
