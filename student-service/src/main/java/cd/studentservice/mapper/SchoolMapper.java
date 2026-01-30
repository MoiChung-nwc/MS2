package cd.studentservice.mapper;

import cd.studentservice.dto.response.SchoolResponse;
import cd.studentservice.entity.School;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface SchoolMapper {
    SchoolResponse toSchoolResponse(School school);
    List<SchoolResponse> toSchoolResponses(List<School>schools);
}
