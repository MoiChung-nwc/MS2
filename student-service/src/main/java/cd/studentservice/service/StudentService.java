package cd.studentservice.service;

import cd.studentservice.dto.request.CreateStudentRequest;
import cd.studentservice.dto.request.UpdateStudentRequest;
import cd.studentservice.dto.response.StudentResponse;
import cd.studentservice.entity.Student;
import com.savvy.common.dto.PageResponse;

public interface StudentService {
    PageResponse<StudentResponse>getPage(int size, int page);
    Student findById(Long id);
    Student save(CreateStudentRequest request);
    Student update(Long id, UpdateStudentRequest request);
    void delete(Long id);
}
