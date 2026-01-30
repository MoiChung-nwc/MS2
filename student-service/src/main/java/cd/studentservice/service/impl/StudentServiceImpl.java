package cd.studentservice.service.impl;

import cd.studentservice.dto.request.CreateStudentRequest;
import cd.studentservice.dto.request.UpdateStudentRequest;
import cd.studentservice.dto.response.StudentResponse;
import cd.studentservice.entity.Class;
import cd.studentservice.entity.School;
import cd.studentservice.entity.Student;
import cd.studentservice.mapper.StudentMapper;
import cd.studentservice.repository.StudentRepository;
import cd.studentservice.service.ClassService;
import cd.studentservice.service.SchoolService;
import cd.studentservice.service.StudentService;
import com.savvy.common.dto.PageResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {
    private final StudentRepository studentRepository;
    private final SchoolService schoolService;
    private final ClassService classService;
    private final StudentMapper studentMapper;

    @Override
    @Transactional
    public PageResponse<StudentResponse> getPage(int size, int page) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Student> studentPages = studentRepository.findAll(pageable);
        return new PageResponse<>(
                studentMapper.toStudentResponses(studentPages.getContent()),
                studentPages.getNumber(),
                studentPages.getSize(),
                studentPages.getTotalElements(),
                studentPages.getTotalPages()
        );
    }

    @Override
    public Student findById(Long id) {
        return studentRepository.findById(id).orElseThrow(() -> new RuntimeException("Cannot find student with id: " + id));
    }

    @Override
    public Student save(CreateStudentRequest request) {
        Class existedClass = classService.getReferenceById(request.getClassId());
        Student student = Student.builder()
                .clazz(existedClass)
                .code(request.getCode())
                .fullName(request.getFullName())
                .gender(request.getGender())
                .status(request.getStatus())
                .dob(request.getDob())
                .build();
        return studentRepository.save(student);
    }

    @Override
    public Student update(Long id, UpdateStudentRequest request) {
        Student existedStudent = findById(id);
        existedStudent.setClazz(!Objects.equals(request.getClassId(), existedStudent.getClazz().getId())?classService.getReferenceById(request.getClassId()):existedStudent.getClazz());
        existedStudent.setCode(request.getCode());
        existedStudent.setFullName(request.getFullName());
        existedStudent.setGender(request.getGender());
        existedStudent.setStatus(request.getStatus());
        existedStudent.setDob(request.getDob());
        return studentRepository.save(existedStudent);
    }

    @Override
    public void delete(Long id) {
        Student existedStudent = studentRepository.getReferenceById(id);
        studentRepository.delete(existedStudent);
    }
}
