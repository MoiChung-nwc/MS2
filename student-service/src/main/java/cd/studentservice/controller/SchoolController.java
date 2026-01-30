package cd.studentservice.controller;

import cd.studentservice.dto.request.CreateSchoolRequest;
import cd.studentservice.dto.request.UpdateSchoolRequest;
import cd.studentservice.dto.response.SchoolResponse;
import cd.studentservice.entity.School;
import cd.studentservice.service.SchoolService;
import com.savvy.common.dto.BaseResponse;
import com.savvy.common.dto.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/schools")
@RequiredArgsConstructor
class SchoolController {
    private final SchoolService schoolService;

    @GetMapping
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<PageResponse<SchoolResponse>>>findAll(@RequestParam int page,
                                                             @RequestParam int size){
        return ResponseEntity.ok(BaseResponse.success(schoolService.getPages(size,page),"Get data succes"));
    }

    @PostMapping
    public ResponseEntity<BaseResponse<School>>save(@RequestBody CreateSchoolRequest request){
        return ResponseEntity.ok(BaseResponse.success(schoolService.save(request),"Save data success"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<School>>update(@PathVariable Long id,
                                                      @RequestBody UpdateSchoolRequest request){
        return ResponseEntity.ok(BaseResponse.success(schoolService.update(id,request),"Update data success"));
    }
}
