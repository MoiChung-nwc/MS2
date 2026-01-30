package cd.studentservice.dto.request;

import cd.studentservice.enumerate.SchoolStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateSchoolRequest {
    String code;
    String name;
    String address;
    SchoolStatus status;
}
