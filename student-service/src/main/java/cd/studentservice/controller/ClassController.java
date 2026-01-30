package cd.studentservice.controller;

import cd.studentservice.dto.response.ClassResponse;
import cd.studentservice.service.ClassService;
import com.savvy.common.dto.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RestController
@RequestMapping("/classes")
@RequiredArgsConstructor
class ClassController {
    private final ClassService classService;

    @GetMapping
//    @PreAuthorize("hasAnyRole('SCHOOL_MANAGER','ADMIN')")
    public ResponseEntity<BaseResponse<List<ClassResponse>>>findBySchoolId(@RequestParam Long schoolId){
        return ResponseEntity.ok(BaseResponse.success(classService.findBySchoolId(schoolId),"Getting data success"));
    }
}
