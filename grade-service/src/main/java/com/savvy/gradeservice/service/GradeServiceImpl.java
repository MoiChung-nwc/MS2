package com.savvy.gradeservice.service;

import com.savvy.common.exception.BusinessException;
import com.savvy.common.exception.ErrorCode;
import com.savvy.gradeservice.api.dto.request.UpdateGradeRequest;
import com.savvy.gradeservice.config.UserContext;
import com.savvy.gradeservice.api.dto.request.CreateGradeRequest;
import com.savvy.gradeservice.api.dto.response.GradeItemResponse;
import com.savvy.gradeservice.api.dto.response.GradeResponse;
import com.savvy.gradeservice.api.dto.response.StudentGradesResponse;
import com.savvy.gradeservice.entity.Grade;
import com.savvy.gradeservice.entity.Subject;
import com.savvy.gradeservice.repository.GradeRepository;
import com.savvy.gradeservice.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GradeServiceImpl implements IGradeService {

    private final GradeRepository gradeRepository;
    private final SubjectRepository subjectRepository;


    @Override
    public StudentGradesResponse getStudentGrades(Long studentId, String term, Long schoolId) {
        UserContext context = UserContext.get();

        if (context == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "Unauthenticated");
        }

        // Validate schoolId
        if (schoolId == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "SchoolId is required");
        }

        Long targetStudentId;
        if (context.isStudent()) {
            targetStudentId = context.getStudentId();
            if (targetStudentId == null) {
                throw new BusinessException(ErrorCode.INVALID_INPUT, "Missing X-Student-Id");
            }

            if (studentId != null && !studentId.equals(targetStudentId)) {
                throw new BusinessException(ErrorCode.FORBIDDEN, "Student cannot access other student's grades");
            }
        } else {

            if (studentId == null) {
                throw new BusinessException(ErrorCode.INVALID_INPUT, "studentId is required");
            }
            targetStudentId = studentId;
        }

        if (context.isManager()) {
            if (!context.hasSchoolAccess(schoolId)) {
                throw new BusinessException(ErrorCode.FORBIDDEN, "schoolId not in scope");
            }
        }

        if (context.isStudent()) {
            List<Grade> existingGrades = gradeRepository.findByStudentId(targetStudentId);
            if (!existingGrades.isEmpty()) {
                Long actualSchoolId = existingGrades.get(0).getSchoolId();
                if (!schoolId.equals(actualSchoolId)) {
                    throw new BusinessException(ErrorCode.FORBIDDEN, "SchoolId does not match student's school");
                }
            }
        }

        List<Grade> grades = gradeRepository.findByStudentIdAndTerm(targetStudentId, term)
                .stream()
                .filter(grade -> schoolId.equals(grade.getSchoolId()))
                .toList();

        List<GradeItemResponse> items = grades.stream()
                .map(grade -> GradeItemResponse.builder()
                        .subject(grade.getSubject().getName())
                        .score(grade.getScore())
                        .build())
                .toList();

        return StudentGradesResponse.builder()
                .studentId(targetStudentId)
                .term(term)
                .items(items)
                .build();
    }

    @Override
    public List<GradeResponse> getAllGrades() {
        UserContext context = UserContext.get();
        
        // Debug logging
        log.info("getAllGrades called - UserContext: userId={}, roles={}, permissions={}, canReadGrades={}", 
                context != null ? context.getUserId() : "null", 
                context != null ? context.getRoles() : "null",
                context != null ? context.getPermissions() : "null",
                context != null ? context.canReadGrades() : "null");
        
        // Check permission
        if (!context.canReadGrades()) {
            throw new BusinessException(
                    ErrorCode.FORBIDDEN,
                    "Permission denied: GRADE_READ required"
            );
        }

        List<Grade> grades;
        
        if (context.isAdmin()) {
            // ADMIN can see all grades without restrictions
            grades = gradeRepository.findAll();
        } else if (context.isManager()) {
            // MANAGER can only see grades from schools in their dataScope
            List<Long> accessibleSchoolIds = context.getAccessibleSchoolIds();
            if (accessibleSchoolIds.isEmpty()) {
                throw new BusinessException(
                        ErrorCode.FORBIDDEN,
                        "No accessible schools in dataScope"
                );
            }
            grades = gradeRepository.findBySchoolIdIn(accessibleSchoolIds);
        } else {
            throw new BusinessException(
                    ErrorCode.FORBIDDEN,
                    "Only ADMIN and MANAGER can access all grades"
            );
        }

        return grades.stream()
                .map(grade -> GradeResponse.builder()
                        .id(grade.getId())
                        .schoolId(grade.getSchoolId())
                        .classId(grade.getClassId())
                        .studentId(grade.getStudentId())
                        .term(grade.getTerm())
                        .subject(grade.getSubject().getName())
                        .score(grade.getScore())
                        .build())
                .toList();
    }


    public GradeResponse createGrade(CreateGradeRequest request) {
        UserContext context = UserContext.get();

        // Verify school scope for MANAGER
        if (context.isManager() && !context.hasSchoolAccess(request.getSchoolId())) {
            throw new BusinessException(
                    ErrorCode.FORBIDDEN,
                    "schoolId not in scope"
            );
        }

        // Validate score
        if (request.getScore() == null) {
            throw new BusinessException(
                    ErrorCode.INVALID_INPUT,
                    "Score is required"
            );
        }
        if (request.getScore().compareTo(java.math.BigDecimal.ZERO) < 0 || 
            request.getScore().compareTo(new java.math.BigDecimal("10")) > 0) {
            throw new BusinessException(
                    ErrorCode.INVALID_INPUT,
                    "Score must be between 0 and 10"
            );
        }

        // Find subject by code
        Subject subject = subjectRepository.findByCode(request.getSubject())
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.INVALID_INPUT,
                        "Subject not found: " + request.getSubject()
                ));

        // Check for duplicate grade
        if (gradeRepository.existsByStudentIdAndSubjectIdAndTerm(
                request.getStudentId(),
                subject.getId(),
                request.getTerm())) {
            throw new BusinessException(
                    ErrorCode.GRADE_ALREADY_EXISTS,
                    "unique(student_id, subject_id, term) violated"
            );
        }

        Grade grade = Grade.builder()
                .schoolId(request.getSchoolId())
                .classId(request.getClassId())
                .studentId(request.getStudentId())
                .term(request.getTerm())
                .subject(subject)
                .score(request.getScore())
                .createdBy(context.getUserId())
                .build();

        Grade savedGrade = gradeRepository.save(grade);

        return GradeResponse.builder()
                .id(savedGrade.getId())
                .schoolId(savedGrade.getSchoolId())
                .classId(savedGrade.getClassId())
                .studentId(savedGrade.getStudentId())
                .term(savedGrade.getTerm())
                .subject(savedGrade.getSubject().getName())
                .score(savedGrade.getScore())
                .build();
    }

    public GradeResponse updateGrade(Long gradeId, UpdateGradeRequest request) {
        UserContext context = UserContext.get();

        Grade grade = gradeRepository.findById(gradeId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.INVALID_INPUT,
                        "Grade not found: " + gradeId
                ));

        // Verify school scope for MANAGER
        if (context.isManager() && !context.hasSchoolAccess(grade.getSchoolId())) {
            throw new BusinessException(
                    ErrorCode.FORBIDDEN,
                    "schoolId not in scope"
            );
        }

        // Validate score
        if (request.getScore() == null) {
            throw new BusinessException(
                    ErrorCode.INVALID_INPUT,
                    "Score is required"
            );
        }
        if (request.getScore().compareTo(java.math.BigDecimal.ZERO) < 0 || 
            request.getScore().compareTo(new java.math.BigDecimal("10")) > 0) {
            throw new BusinessException(
                    ErrorCode.INVALID_INPUT,
                    "Score must be between 0 and 10"
            );
        }

        // Update the score
        grade.setScore(request.getScore());
        Grade updatedGrade = gradeRepository.save(grade);

        return GradeResponse.builder()
                .id(updatedGrade.getId())
                .schoolId(updatedGrade.getSchoolId())
                .classId(updatedGrade.getClassId())
                .studentId(updatedGrade.getStudentId())
                .term(updatedGrade.getTerm())
                .subject(updatedGrade.getSubject().getName())
                .score(updatedGrade.getScore())
                .build();
    }
}
