package cd.studentservice.mapper;

import cd.studentservice.dto.response.ClassResponse;
import cd.studentservice.entity.Class;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {
                SchoolMapper.class,
        })
public interface ClassMapper {
    ClassResponse toClassResponse(Class clazz);
    List<ClassResponse>toClassResponses(List<Class>classes);
}
