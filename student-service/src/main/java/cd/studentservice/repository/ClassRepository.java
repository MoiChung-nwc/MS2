package cd.studentservice.repository;

import cd.studentservice.entity.Class;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClassRepository extends JpaRepository<Class, Long> {
    List<Class>findAllBySchoolId(Long schoolId);
}