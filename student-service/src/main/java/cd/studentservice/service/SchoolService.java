package cd.studentservice.service;

import cd.studentservice.dto.request.CreateSchoolRequest;
import cd.studentservice.dto.request.CreateStudentRequest;
import cd.studentservice.dto.request.UpdateSchoolRequest;
import cd.studentservice.dto.response.SchoolResponse;
import cd.studentservice.entity.School;
import com.savvy.common.dto.PageResponse;

public interface SchoolService {
    School findById(Long id);
    School getReferenceById(Long id);
    PageResponse<SchoolResponse>getPages(int size,int page);
    School save(CreateSchoolRequest request);
    School update(Long id, UpdateSchoolRequest request);
}
