package cd.studentservice.service.impl;

import cd.studentservice.dto.request.CreateSchoolRequest;
import cd.studentservice.dto.request.CreateStudentRequest;
import cd.studentservice.dto.request.UpdateSchoolRequest;
import cd.studentservice.dto.response.SchoolResponse;
import cd.studentservice.entity.School;
import cd.studentservice.mapper.SchoolMapper;
import cd.studentservice.repository.SchoolRepository;
import cd.studentservice.service.SchoolService;
import com.savvy.common.dto.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SchoolServiceImpl implements SchoolService {
    private final SchoolRepository schoolRepository;
    private final SchoolMapper schoolMapper;

    @Override
    public School findById(Long id) {
        return schoolRepository.findById(id).orElseThrow(() -> new RuntimeException("Cannot find school with id: " + id));
    }

    @Override
    public School getReferenceById(Long id) {
        return schoolRepository.getReferenceById(id);
    }

    @Override
    public PageResponse<SchoolResponse> getPages(int size, int page) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<School> schoolPages = schoolRepository.findAll(pageable);
        return new PageResponse<>(
                schoolMapper.toSchoolResponses(schoolPages.getContent()),
                schoolPages.getNumber(),
                schoolPages.getSize(),
                schoolPages.getTotalElements(),
                schoolPages.getTotalPages()
                );
    }



    @Override
    public School save(CreateSchoolRequest request) {
        School school= School.builder()
                .code(request.getCode())
                .name(request.getName())
                .address(request.getAddress())
                .status(request.getStatus())
                .build();
        return schoolRepository.save(school);
    }

    @Override
    public School update(Long id, UpdateSchoolRequest request) {
        School existedSchool=findById(id);
        existedSchool.setCode(request.getCode());
        existedSchool.setName(request.getName());
        existedSchool.setAddress(request.getAddress());
        existedSchool.setStatus(request.getStatus());
        return schoolRepository.save(existedSchool);
    }
}
