package cd.studentservice.service;

import cd.studentservice.dto.response.ClassResponse;
import cd.studentservice.entity.Class;
import com.savvy.common.dto.PageResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ClassService {
    Class findById(Long id);
    List<ClassResponse> findBySchoolId(Long schoolId);
    Class getReferenceById(Long id);
}
