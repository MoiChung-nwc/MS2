package cd.studentservice.repository;

import cd.studentservice.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {
    @Override
    @EntityGraph(attributePaths = {
            "clazz","clazz.school"
    })
    Page<Student> findAll(Pageable pageable);
}