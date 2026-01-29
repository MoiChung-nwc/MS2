package com.savvy.gradeservice.service;

import com.savvy.common.exception.BusinessException;
import com.savvy.common.exception.ErrorCode;
import com.savvy.gradeservice.config.UserContext;
import com.savvy.gradeservice.dto.request.CreateGradeRequest;
import com.savvy.gradeservice.dto.response.GradeItemResponse;
import com.savvy.gradeservice.dto.response.GradeResponse;
import com.savvy.gradeservice.dto.response.StudentGradesResponse;
import com.savvy.gradeservice.entity.Grade;
import com.savvy.gradeservice.entity.Subject;
import com.savvy.gradeservice.repository.GradeRepository;
import com.savvy.gradeservice.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GradeService {

    private final GradeRepository gradeRepository;
    private final SubjectRepository subjectRepository;


    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyAuthority('STUDENT', 'SCHOOL_MANAGER', 'ADMIN')")
    public StudentGradesResponse getStudentGrades(Long studentId, String term) {
        UserContext context = UserContext.get();

        if (context.isStudent()) {
            if (!studentId.equals(context.getUserId())) {
                throw new BusinessException(
                        ErrorCode.FORBIDDEN,
                        "Student cannot access other student's grades"
                );
            }
        }
        else if (context.isManager()) {
            // Find student's school and verify manager has access
            List<Grade> existingGrades = gradeRepository.findByStudentId(studentId);
            if (!existingGrades.isEmpty()) {
                Long schoolId = existingGrades.get(0).getSchoolId();
                if (!context.hasSchoolAccess(schoolId)) {
                    throw new BusinessException(
                            ErrorCode.FORBIDDEN,
                            "schoolId not in scope"
                    );
                }
            }
        }


        List<Grade> grades = gradeRepository.findByStudentIdAndTerm(studentId, term);
        
        List<GradeItemResponse> items = grades.stream()
                .map(grade -> GradeItemResponse.builder()
                        .subject(grade.getSubject().getName())
                        .score(grade.getScore())
                        .build())
                .toList();

        return StudentGradesResponse.builder()
                .studentId(studentId)
                .term(term)
                .items(items)
                .build();
    }

    @Transactional
    @PreAuthorize("hasAnyAuthority('SCHOOL_MANAGER', 'ADMIN')")
    public GradeResponse createGrade(CreateGradeRequest request) {
        UserContext context = UserContext.get();

        // Verify school scope for MANAGER
        if (context.isManager() && !context.hasSchoolAccess(request.getSchoolId())) {
            throw new BusinessException(
                    ErrorCode.FORBIDDEN,
                    "schoolId not in scope"
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
}
