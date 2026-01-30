package com.savvy.gradeservice.repository;

import com.savvy.gradeservice.entity.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {
    
    List<Grade> findByStudentIdAndTerm(Long studentId, String term);
    
    List<Grade> findByStudentId(Long studentId);

    List<Grade> findBySchoolIdIn(List<Long> schoolIds);

    boolean existsByStudentIdAndSubjectIdAndTerm(Long studentId, Long subjectId, String term);

}
