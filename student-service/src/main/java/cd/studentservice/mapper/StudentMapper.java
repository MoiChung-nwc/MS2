package cd.studentservice.mapper;

import cd.studentservice.dto.response.StudentResponse;
import cd.studentservice.entity.Student;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {
                ClassMapper.class
        })
public interface StudentMapper {
    StudentResponse toStudentResponse(Student student);
    List<StudentResponse> toStudentResponses(List<Student> students);
}
