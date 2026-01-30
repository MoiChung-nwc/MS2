package cd.studentservice.dto.request;

import cd.studentservice.enumerate.StudentStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateStudentRequest {
    String code;
    String fullName;
    LocalDate dob;
    Boolean gender;
    StudentStatus status;
    Long schoolId;
    Long classId;
}
